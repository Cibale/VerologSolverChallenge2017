#! /usr/bin/env python

import argparse, copy
import xml.etree.ElementTree as ET
from InstanceCVRPTWUI import InstanceCVRPTWUI as InstanceCVRPTWUI
import baseCVRPTWUI as base
from collections import OrderedDict
from pprint import pprint as pprint


class SolutionCVRPTWUI(base.BaseParser):
    parsertype = 'solution'
    
    class LANG:
        class TXT:
            dataset = 'DATASET'
            name = 'NAME'
            maxNumVehicles = 'MAX_NUMBER_OF_VEHICLES'
            numVehicleDays = 'NUMBER_OF_VEHICLE_DAYS'
            distance = 'DISTANCE'
            cost = 'COST'
            toolUse = 'TOOL_USE'
            nofVehicles = 'NUMBER_OF_VEHICLES'
            startDepot = 'START_DEPOT'
            finishDepot = 'FINISH_DEPOT'
            costfields  = OrderedDict( [ (  maxNumVehicles,   'MaxNumberOfVehicles'),
                                         (  numVehicleDays,   'NumberOfVehicleDays'),
                                         (  toolUse,          'ToolCount'),
                                         (  distance,         'Distance'),
                                         (  cost,             'Cost' ) ] )
            dayfields   = OrderedDict( [ (  nofVehicles,      'GivenNumberOfVehicles'),
                                         (  startDepot,       'givenStartDepot'),
                                         (  finishDepot,      'givenFinishDepot') ] )
            day = 'DAY'
        class XML:
            solution = 'solution'
            info = 'info'
            dataset = 'dataset'
            name = 'name'
            cost = 'cost'
            maxNumVehicles = 'max_number_of_vehicles'
            numVehicleDays = 'number_of_vehicle_days'
            distance = 'distance'
            costfields  = OrderedDict( [ (  maxNumVehicles,   'MaxNumberOfVehicles'),
                                         (  numVehicleDays,   'NumberOfVehicleDays'),
                                         (  distance,         'Distance'),
                                         (  cost,             'Cost' ) ] )
            costValue = 'cost_value'
            tools = 'tools'
            tool = 'tool'
            attr_id = 'id'
            used = 'used'
            days = 'days'
            day = 'day'
            attr_nofVehicles = 'number_of_vehicles'
            startDepot = 'start_depot'
            finishDepot = 'finish_depot'
            vehicles = 'vehicles'
            vehicle = 'vehicle'
            route = 'route'
            request = 'request'
            depot = 'depot'
            attr_type = 'type'
            pickup = 'pickup'
            deliver = 'deliver'
            visits = 'visit'
    class SolutionCost(object):
        def __init__(self):
            self.MaxNumberOfVehicles = None
            self.NumberOfVehicleDays = None
            self.Distance = None
            self.ToolCount = None
            self.Cost = None
        def calculateCost(self,instance):
            cost = self.MaxNumberOfVehicles * instance.VehicleCost
            cost += self.NumberOfVehicleDays * instance.VehicleDayCost
            cost += self.Distance * instance.DistanceCost
            for i in range(len(instance.Tools)):
                cost += self.ToolCount[i] * instance.Tools[i].cost
            self.Cost = cost
            
        def __str__(self):
            if not self.MaxNumberOfVehicles or not self.NumberOfVehicleDays or not self.Distance or not self.ToolCount or not self.Cost:
                return 'Max number of vehicles: %r\nNumber of vehicle days: %r\nDistance: %r\nCost: %r\nTool count: %r' % (self.MaxNumberOfVehicles, self.NumberOfVehicleDays, self.Distance, self.Cost, self.ToolCount )
            else:
                return 'Max number of vehicles: %d\nNumber of vehicle days: %d\nDistance: %d\nCost: %d\nTool count: %r' % (self.MaxNumberOfVehicles, self.NumberOfVehicleDays, self.Distance, self.Cost, self.ToolCount )
    
    class SolutionVehicle(object):
        def __init__(self):
            self.Route = []
            self.calcDistance = None
            self.givenDistance = None
            self.calcVisits = []
            self.givenVisits = []
        def __str__(self):
            return 'R: %r\nCD: %r\tGD: %r\nCV: %r\nGV: %r' % (self.Route, self.calcDistance, self.givenDistance, self.calcVisits, self.givenVisits )
            
    class SolutionDay(object):
        def __init__(self, dayNr):
            self.dayNumber = dayNr
            self.GivenNumberOfVehicles = None
            self.Vehicles = []
            self.givenStartDepot = None
            self.givenFinishDepot = None
            self.calcStartDepot = None
            self.calcFinishDepot = None
        def __str__(self):
            strRepr = 'Day: %d' % self.dayNumber
            if self.GivenNumberOfVehicles is not None:
                strRepr += '\tGnofV: %d' % self.GivenNumberOfVehicles
            if self.givenStartDepot is not None:
                strRepr += '\nGSD: %r' % self.givenStartDepot
            if self.givenFinishDepot is not None:
                strRepr += '\nGFD: %r' % self.givenFinishDepot
            if self.calcStartDepot is not None:
                strRepr += '\nCSD: %r' % self.calcStartDepot
            if self.calcFinishDepot is not None:
                strRepr += '\nCFD: %r' % self.calcFinishDepot
            strRepr += '\nVEHICLES:'
            for i in range(len(self.Vehicles)):
                strRepr += '\nVehicle: %d\n%s' % ( i, str(self.Vehicles[i]) )
            return strRepr
            
    def __str__(self):
        strRepr = 'GivenCost: %s\nCalcCost: %s\nDAYS:' % (str(self.givenCost),str(self.calcCost))
        for day in self.Days:
                strRepr += '\n%s\n' % ( str(day) )        
        return strRepr
    
    def __init__(self, inputfile,Instance,filetype=None,continueOnErr=False):
        self.Instance = Instance
        self.Instance.calculateDistances()
        self._doinit(inputfile,filetype,continueOnErr)
        if self.isValid():
            self._calculateSolution()
        
    def _initData(self):
        self.Days = []
        self.givenCost = self.SolutionCost()
        self.calcCost = self.SolutionCost()
        
    def _parseToolsLine(self,field,line):
        ToolsLine = line.split()
        nofTools = len(self.Instance.Tools)
        self._checkError("Expected %d integers on a tools line (%s). Found: '%s'." % (nofTools,field,line), len(ToolsLine) == nofTools)
        try:
            value = [int(x) for x in ToolsLine]
        except ValueError as err:
            toolfield = err.message.split(':',1).strip().strip("'")
            self._checkError('Expected %d integers on the %s line. Found incorrect data (%s): %s.' % (nofTools,field,toolfield,line),False)
        except:
            self._checkError('Expected %d integers on the %s line. Found incorrect data: %s.' % (nofTools,field,line),False)
        return value
        
    def _readTextCost(self, fd, lastLineAssignment = None):
        if not lastLineAssignment:
            lastLineAssignment = self._isAssignment(fd)
        if lastLineAssignment:
            for field, member in self.LANG.TXT.costfields.items():
                if lastLineAssignment is None or lastLineAssignment[0] is None or lastLineAssignment[0] == self.LANG.TXT.day:
                    return lastLineAssignment
                if lastLineAssignment[0] == field:
                    value = lastLineAssignment[1]
                    if field != self.LANG.TXT.toolUse:
                        value = self._checkInt(field,value)
                    else:
                        value = self._parseToolsLine(field,value)
                    self.givenCost.__setattr__(member,value)
                lastLineAssignment = self._isAssignment(fd)
            if lastLineAssignment is None or lastLineAssignment[0] is None or lastLineAssignment[0] == self.LANG.TXT.day:
                    return lastLineAssignment
            self._checkError('Unexpected field: %s.' % lastLineAssignment[0],False)
        return lastLineAssignment
    
    def _readDay(self, fd, lastLineAssignment):
        self._checkError('Unexpected string: %s.' % lastLineAssignment[1], lastLineAssignment[0] is not None )
        self._checkError('Unexpected field: %s.' % lastLineAssignment[0],lastLineAssignment[0] == self.LANG.TXT.day)
        newDay = self.SolutionDay(self._checkInt(self.LANG.TXT.day,lastLineAssignment[1]))
        self._checkError('Day number should be positive, found %d.' % (newDay.dayNumber), newDay.dayNumber > 0 )
        self._checkError('Day number should be at most %d, found %d.' % (self.Instance.Days,newDay.dayNumber), newDay.dayNumber <= self.Instance.Days )
        lastDay = self.Days[-1].dayNumber if len(self.Days) > 0 else 0
        self._checkError('Incorrect order of days, found day %d after day %d.' % (newDay.dayNumber, lastDay), newDay.dayNumber > lastDay )
        lastLineAssignment = self._isAssignment(fd)
        for field, member in self.LANG.TXT.dayfields.items():
            if lastLineAssignment is None or lastLineAssignment[0] is None:
                break
            if lastLineAssignment[0] == field:
                value = lastLineAssignment[1]
                if field == self.LANG.TXT.nofVehicles:
                    value = self._checkInt(field,value)
                else:
                    value = self._parseToolsLine(field,value)
                newDay.__setattr__(member,value)
                lastLineAssignment = self._isAssignment(fd)
        self._checkError('Expected a route line (day %d)' % newDay.dayNumber, lastLineAssignment is None or lastLineAssignment[0] is None or lastLineAssignment[0] == self.LANG.TXT.day )
        while lastLineAssignment is not None and lastLineAssignment[0] is None:
            line = lastLineAssignment[1]
            vehLine = line.split()
            self._checkError('Expected a route/visit/distance line (day %d)' % newDay.dayNumber, len(vehLine) >= 3 )
            vehNum = self._checkInt('vehicle number', vehLine[0], '(day %d) ' % newDay.dayNumber)
            if vehLine[1] == 'R':
                self._checkError('Expected route %d, found %d (day %d)' % (len(newDay.Vehicles) + 1,vehNum,newDay.dayNumber), len(newDay.Vehicles) + 1 == vehNum )
                veh = self.SolutionVehicle()
                try:
                    veh.Route = [int(x) for x in vehLine[2:]]
                except ValueError as err:
                    routefield = err.message.split(':',1).strip().strip("'")
                    self._checkError('Expected integers on the route line (day %d). Found incorrect data (%s): %s.' % (newDay.dayNumber,routefield,line),False)
                except:
                    self._checkError('Expected integers on the route line (day %d). Found incorrect data: %s.' % (newDay.dayNumber,line),False)
                self._checkError('Route should be at least length 3, found %d (day %d).' % (len(veh.Route),newDay.dayNumber),len(veh.Route)>=3)
                self._checkError('Route should start at the depot (day %d).' % (newDay.dayNumber),veh.Route[0] == 0)
                self._checkError('Route should end at the depot (day %d).' % (newDay.dayNumber),veh.Route[-1] == 0)
                newDay.Vehicles.append(veh)
            elif vehLine[1] == 'V':
                self._checkError('Expected a visit line for route %d (day %d), found %d' % (len(newDay.Vehicles),newDay.dayNumber,vehNum), len(newDay.Vehicles) == vehNum )
                visitnum = self._checkInt('visit number',vehLine[2],'(day %d, route %d) '%(newDay.dayNumber,vehNum))
                veh = newDay.Vehicles[-1]
                self._checkError('Expected visit line %d (day %d, route %d), found %d' % (len(veh.givenVisits) + 1, newDay.dayNumber, vehNum, visitnum), len(veh.givenVisits) + 1 == visitnum )
                veh.givenVisits.append( self._parseToolsLine('Visit %d (day %d, route %d)' %(visitnum,newDay.dayNumber, vehNum), ' '.join(vehLine[3:]) ) )
            elif vehLine[1] == 'D':
                self._checkError('Expected a distance for route %d, found %d' % (len(newDay.Vehicles),vehNum), len(newDay.Vehicles) == vehNum )
                veh = newDay.Vehicles[-1]
                self._checkError('Expected a distance line (day %d, route %d), found %s' % (newDay.dayNumber,vehNum, line), len(vehLine) == 3 )
                self._checkError('Found a second distance line (day %d, route %d), found %s' % (newDay.dayNumber,vehNum, line), veh.givenDistance == None )
                veh.givenDistance = self._checkInt('Distance (day %d, route %d)' % (newDay.dayNumber,vehNum), vehLine[2] )
            else:
                self._checkError('Expected a Route/Visit/Distance line, found %s' % vehLine[1], False )
            lastLineAssignment = self._isAssignment(fd)
        self.Days.append(newDay) 
        if len(newDay.Vehicles) == 0:
            self.warningReport.append( 'Empty day %d' % newDay.dayNumber )
        return lastLineAssignment
    
    def _initTXT(self):
        try:
            fd = open(self.inputfile, 'r')
        except:
            self.errorReport.append( 'Solution file %s could not be read.' % self.inputfile )
            return
        
        try:
            with fd:
                self.Dataset = self._checkAssignment(fd,self.LANG.TXT.dataset,'string')
                self.Name = self._checkAssignment(fd,self.LANG.TXT.name,'string')
                
                assignment = self._readTextCost(fd)
                while assignment:
                    assignment = self._readDay(fd,assignment)
                self.Days = [d for d in self.Days if len(d.Vehicles) > 0]

        except self.BaseParseException:
            pass
        except:
            print('Crash during CVRPTWUI solution reading\nThe following errors were found:')
            print( '\t' + '\n\t'.join(self.errorReport) )
            raise
        
    def _parseToolsTag(self,tagWithTools,extra):
        usedTools = []
        Num_tools = 0
        nofTools = len(self.Instance.Tools)
        for tool in tagWithTools.findall(self.LANG.XML.tool):
            Num_tools += 1
            toolID = self._checkInt('Tool ID', self._findAttribute(tool, self.LANG.XML.attr_id), extra + ' ' )
            self._checkError('The indexing of the Tools is incorrect at Tool nr. %d (%s).' % (toolID, extra), toolID == Num_tools )
            used = self._checkInt('Tools used', tool.text, 'for tool %d %s ' % (toolID,extra) )
            usedTools.append(used)
        self._checkError("Expected %d tools (%s). Found: %d." % (nofTools,extra,Num_tools), Num_tools == nofTools)
        return usedTools
    
    def _initXML(self):
        try:
            fd = open(self.inputfile, 'r')
        except:
            self.errorReport.append( 'Solution file %s could not be read.' % self.inputfile )
            return
        
        try:
            with fd:
                tree = ET.parse(fd)
                root = tree.getroot()                
                self._checkError('Root tag is not equal to solution.',root.tag == self.LANG.XML.solution)
                
                info = self._findTag(root, self.LANG.XML.info )
                self.Dataset = self._findTag(info, self.LANG.XML.dataset ).text
                self.Name = self._findTag(info, self.LANG.XML.name ).text
                
                cost = root.find( self.LANG.XML.cost )
                if cost is not None:
                    for field, member in self.LANG.XML.costfields.items():
                        foundField = cost.find( field )
                        if foundField is not None:
                            value = self._checkInt(field, foundField.text )
                            self.givenCost.__setattr__(member,value)
                    foundTools = cost.find( self.LANG.XML.tools )
                    if foundTools is not None:
                        self.givenCost.ToolCount = self._parseToolsTag(foundTools,'in cost tools tag')
                
                days = self._findTag(root, self.LANG.XML.days )
                for day in days.findall(self.LANG.XML.day):
                    newDay = self.SolutionDay(self._checkInt('Day id', self._findAttribute(day, self.LANG.XML.attr_id)))
                    self._checkError('Day number should be positive, found %d.' % (newDay.dayNumber), newDay.dayNumber > 0 )
                    self._checkError('Day number should be at most %d, found %d.' % (self.Instance.Days,newDay.dayNumber), newDay.dayNumber <= self.Instance.Days )
                    lastDay = self.Days[-1].dayNumber if len(self.Days) > 0 else 0
                    self._checkError('Incorrect order of days, found day %d after day %d.' % (newDay.dayNumber, lastDay), newDay.dayNumber > lastDay )
                    startDepot = day.find( self.LANG.XML.startDepot )
                    finishDepot = day.find( self.LANG.XML.finishDepot )
                    if startDepot is not None:
                        newDay.givenStartDepot = self._parseToolsTag(startDepot,'in %s tag of day %d' % (self.LANG.XML.startDepot,newDay.dayNumber) )
                    if finishDepot is not None:
                        newDay.givenFinishDepot = self._parseToolsTag(finishDepot,'in %s tag of day %d' % (self.LANG.XML.finishDepot,newDay.dayNumber) )
                    vehicles = self._findTag(day, self.LANG.XML.vehicles )
                    if self.LANG.XML.attr_nofVehicles in vehicles.attrib:
                        newDay.GivenNumberOfVehicles = self._checkInt('Number of vehicles',vehicles.attrib[self.LANG.XML.attr_nofVehicles], '(day %d) ' % newDay.dayNumber )
                    Num_vehicles = 0
                    for vehicle in vehicles.findall(self.LANG.XML.vehicle):
                        Num_vehicles += 1
                        vehicleID = self._checkInt('Vehicle ID', self._findAttribute(vehicle, self.LANG.XML.attr_id), 'of day %d ' % (newDay.dayNumber) )
                        self._checkError('The indexing of the Vehicle is incorrect at Vehicle nr. %d of day %d.' % (vehicleID, newDay.dayNumber), vehicleID == Num_vehicles )
                        veh = self.SolutionVehicle()
                        distance = vehicle.find( self.LANG.XML.distance )
                        if distance is not None:
                            veh.givenDistance = self._checkInt('Distance of vehicle %d of day %d ' % (vehicleID,newDay.dayNumber), distance.text )
                        route = self._findTag(vehicle, self.LANG.XML.route )
                        for child in route:
                            if child.tag == self.LANG.XML.depot:
                                veh.Route.append(0)
                                if len(child):
                                    veh.givenVisits.append( self._parseToolsTag(child,'in %s tag of vehicle %d of day %d' % (self.LANG.XML.depot,vehicleID,newDay.dayNumber) ) )
                            if child.tag == self.LANG.XML.request:
                                typeAttr = self._findAttribute(child, self.LANG.XML.attr_type)
                                self._checkError("The type of a reqeust should be '%s' or '%s' (vehicle %d of day %d), found '%s'." % (self.LANG.XML.pickup,self.LANG.XML.deliver,vehicleID,newDay.dayNumber,typeAttr), typeAttr == self.LANG.XML.pickup or typeAttr == self.LANG.XML.deliver )
                                request = self._checkInt('Request', child.text, 'of vehicle %d of day %d ' % (vehicleID,newDay.dayNumber) )
                                veh.Route.append(request if typeAttr == self.LANG.XML.deliver else -request)
                        self._checkError('Route should be at least length 3, found %d (vehicle %d of day %d).' % (len(veh.Route),vehicleID,newDay.dayNumber),len(veh.Route)>=3)
                        self._checkError('Route should start at the depot (vehicle %d of day %d).' % (vehicleID,newDay.dayNumber),veh.Route[0] == 0)
                        self._checkError('Route should end at the depot (vehicle %d of day %d).' % (vehicleID,newDay.dayNumber),veh.Route[-1] == 0)
                        newDay.Vehicles.append(veh)
                    self.Days.append(newDay)
                             
        except self.BaseParseException:
            pass
        except:
            print('Crash during CVRPTWUI solution reading\nThe following errors were found:')
            print( '\t' + '\n\t'.join(self.errorReport) )
            raise
    
    def _calculateSolution(self):
        try:
            totalDistance = 0
            maxNumVehicles = 0
            dayNumVehicles = 0
            RequestDeliver = [None] * (len(self.Instance.Requests) + 1 )
            RequestPickup  = [None] * (len(self.Instance.Requests) + 1 )
            toolUse     = [0] * len(self.Instance.Tools)    
            toolStatus  = [0] * len(self.Instance.Tools)
            toolSize = [t.weight for t in self.Instance.Tools]
            for day in self.Days:
                day.calcStartDepot = [0] * len(self.Instance.Tools)
                day.calcFinishDepot = [0] * len(self.Instance.Tools)
                maxNumVehicles = max(maxNumVehicles,len(day.Vehicles))
                dayNumVehicles += len(day.Vehicles)
                for i in range(len(day.Vehicles)):
                    vehicle = day.Vehicles[i]
                    distance = 0
                    lastNode = None
                    currentTools = [0] * len(self.Instance.Tools)
                    depotVisits = [[0] * len(self.Instance.Tools)]
                    nodeVisits = []
                    for node in vehicle.Route:
                        if node == 0:
                            if lastNode is not None:
                                if lastNode == 0:
                                    self._checkError('Two consecutive depot visits at vehicle %d of day %d.' % (i+1,day.dayNumber), False )
                                bringTools  = [0] * len(self.Instance.Tools)
                                sumTools    = [0] * len(self.Instance.Tools)
                                for tools in nodeVisits:
                                    sumTools    = [max(x) for x in zip(tools, sumTools)]
                                    bringTools  = [min(x) for x in zip(bringTools, tools)]
                                    sumTools    = [max(0, a) for a in sumTools]
                                depotVisits[-1] = [sum(x) for x in zip(bringTools, depotVisits[-1])]
                                
                                loaded = sum([a*-b for a,b in zip(toolSize,depotVisits[-1])])
                                self._checkError('Capacity exceeded at vehicle %d of day %d, found %d (maximum %d).' % (i+1, day.dayNumber, loaded, self.Instance.Capacity), loaded <= self.Instance.Capacity )
                                for tools in nodeVisits:
                                    loaded = sum([a*(b-c) for a,b,c in zip(toolSize,tools,depotVisits[-1])])
                                    self._checkError('Capacity exceeded at vehicle %d of day %d, found %d (maximum %d).' % (i+1, day.dayNumber, loaded, self.Instance.Capacity), loaded <= self.Instance.Capacity )
                                depotVisits.append([b-a for a,b in zip(bringTools, nodeVisits[-1])])
                                currentTools = [0] * len(self.Instance.Tools)
                                nodeVisits = []
                        elif node > 0:
                            self._checkError('Unknown request %d (current day %d).' % (node,day.dayNumber), node < len(RequestDeliver) )
                            self._checkError('Deliver of request %d is already planned on day %d (current day %d).' % (node,RequestDeliver[node] if RequestDeliver[node] is not None else 0,day.dayNumber), RequestDeliver[node] == None )
                            RequestDeliver[node] = day.dayNumber
                            currentTools[self.Instance.Requests[node-1].tool-1] -= self.Instance.Requests[node-1].toolCount
                        elif node < 0:
                            node = - node
                            self._checkError('Unknown request %d (current day %d).' % (node,day.dayNumber), node < len(RequestPickup) )
                            self._checkError('Pickup of request %d is already planned on day %d (current day %d).' % (node,RequestPickup[node] if RequestPickup[node] is not None else 0,day.dayNumber), RequestPickup[node] == None )
                            RequestPickup[node] = day.dayNumber
                            currentTools[self.Instance.Requests[node-1].tool-1] += self.Instance.Requests[node-1].toolCount
                        nodeVisits.append(copy.copy(currentTools))
                        if lastNode is not None:
                            fromCoord = self.Instance.DepotCoordinate if lastNode == 0 else self.Instance.Requests[lastNode-1].node
                            toCoord = self.Instance.DepotCoordinate if node == 0 else self.Instance.Requests[node-1].node
                            distance += self.Instance.calcDistance[fromCoord][toCoord]
                        lastNode = node
                    distance += self.Instance.calcDistance[toCoord][self.Instance.DepotCoordinate]
                    vehicle.calcDistance = distance
                    self._checkError('Distance of vehicle %d is exceeded, %d (maximum %d) (current day %d).' % (i+1,distance,self.Instance.MaxDistance,day.dayNumber), distance <= self.Instance.MaxDistance )
                    vehicle.calcVisits = depotVisits
                    totalDistance += distance
                    visitTotal          = [0] * len(self.Instance.Tools)
                    totalUsedAtStart    = [0] * len(self.Instance.Tools)
                    for visit in vehicle.calcVisits:
                        visitTotal = [sum(x) for x in zip(visit, visitTotal)]
                        totalUsedAtStart    = [b - min(0, a) for a,b in zip(visitTotal, totalUsedAtStart)]
                        visitTotal          = [max(0, a) for a in visitTotal]
                    day.calcStartDepot  = [b - a for a,b in zip(totalUsedAtStart, day.calcStartDepot)]
                    day.calcFinishDepot = [b + a for a,b in zip(visitTotal, day.calcFinishDepot)]
                toolStatus = [sum(x) for x in zip(toolStatus, day.calcStartDepot)]
                toolUse = [max(-a,b) for a, b in zip(toolStatus, toolUse)]
                toolStatus = [sum(x) for x in zip(toolStatus, day.calcFinishDepot)]
            
            for i in range(1,len(self.Instance.Requests)+1):
                self._checkError('Deliver for request %d is not executed.' % i, RequestDeliver[i] is not None)
                self._checkError('Pickup for request %d is not executed.' % i, RequestPickup[i] is not None)
                if RequestDeliver[i] is not None and RequestPickup[i] is not None:
                    self._checkError('Number of days between deliver and pickup is not correct for request %d, found %d instead of %d.' % (i,RequestPickup[i] - RequestDeliver[i],self.Instance.Requests[i-1].numDays), RequestPickup[i] - RequestDeliver[i] == self.Instance.Requests[i-1].numDays)
                    self._checkError('Deliver is not planned on a correct day for request %d, found %d instead of %d-%d.' % (i,RequestDeliver[i],self.Instance.Requests[i-1].fromDay,self.Instance.Requests[i-1].toDay), self.Instance.Requests[i-1].fromDay <= RequestDeliver[i] <= self.Instance.Requests[i-1].toDay)
            
            for i in range(len(self.Instance.Tools)):
                self._checkError('Number of tools used is too high for tool %d, found %d (maximum %d).' % (i+1,toolUse[i],self.Instance.Tools[i].amount), toolUse[i] <= self.Instance.Tools[i].amount)
            
            self.calcCost.MaxNumberOfVehicles = maxNumVehicles
            self.calcCost.NumberOfVehicleDays = dayNumVehicles
            self.calcCost.Distance = totalDistance
            self.calcCost.ToolCount = toolUse        
            self.calcCost.calculateCost(self.Instance)
    
            toolStatus = toolUse
            for day in self.Days:
                toolStatus = [sum(x) for x in zip(toolStatus, day.calcStartDepot)]
                day.calcStartDepot = toolStatus
                toolStatus = [sum(x) for x in zip(toolStatus, day.calcFinishDepot)]
                day.calcFinishDepot = toolStatus
            
            assert (not self.isValid() or toolStatus == toolUse), 'ALL TOOLS SHOULD BE RETURNED'
        except self.BaseParseException:
            pass
        except:
            print('Crash during CVRPTWUI solution calculation\nThe following errors were found:')
            print( '\t' + '\n\t'.join(self.errorReport) )
            raise
        
    def isValid(self):
        return not self.errorReport

    def areGivenValuesValid(self):
        try:
            self._checkError('Incorrect max number of vehicles (%d instead of %d).' % (self.givenCost.MaxNumberOfVehicles if self.givenCost.MaxNumberOfVehicles is not None else 0,self.calcCost.MaxNumberOfVehicles), self.givenCost.MaxNumberOfVehicles is None or self.givenCost.MaxNumberOfVehicles == self.calcCost.MaxNumberOfVehicles )
            self._checkError('Incorrect number of day-vehicles (%d instead of %d).' % (self.givenCost.NumberOfVehicleDays if self.givenCost.NumberOfVehicleDays is not None else 0,self.calcCost.NumberOfVehicleDays), self.givenCost.MaxNumberOfVehicles is None or self.givenCost.NumberOfVehicleDays == self.calcCost.NumberOfVehicleDays )
            self._checkError('Incorrect distance (%d instead of %d).' % (self.givenCost.Distance if self.givenCost.Distance is not None else 0,self.calcCost.Distance), self.givenCost.Distance is None or self.givenCost.Distance == self.calcCost.Distance )
            self._checkError('Incorrect cost (%d instead of %d).' % (self.givenCost.Cost if self.givenCost.Cost is not None else 0,self.calcCost.Cost), self.givenCost.Cost is None or self.givenCost.Cost == self.calcCost.Cost )
            if self.givenCost.ToolCount is not None:
                for i in range(len(self.calcCost.ToolCount)):
                    self._checkError('Incorrect tool count for tool %d (%d instead of %d).' % (i+1,self.givenCost.ToolCount[i],self.calcCost.ToolCount[i]), self.givenCost.ToolCount[i] == self.calcCost.ToolCount[i] )
            for day in self.Days:
                self._checkError('Incorrect number of vehicles for day %d (%d instead of %d).' % (day.dayNumber,day.GivenNumberOfVehicles if day.GivenNumberOfVehicles is not None else 0, len(day.Vehicles) ), day.GivenNumberOfVehicles is None or day.GivenNumberOfVehicles == len(day.Vehicles) )
                if day.givenStartDepot is not None:
                    for i in range(len(day.calcStartDepot)):
                        self._checkError('Incorrect tool count after the start of day %d for tool %d (%d instead of %d).' % (day.dayNumber,i+1,day.givenStartDepot[i],day.calcStartDepot[i]), day.givenStartDepot[i] == day.calcStartDepot[i] )
                if day.givenFinishDepot is not None:
                    for i in range(len(day.calcFinishDepot)):
                        self._checkError('Incorrect tool count after the finish of day %d for tool %d (%d instead of %d).' % (day.dayNumber,i+1,day.givenFinishDepot[i],day.calcFinishDepot[i]), day.givenFinishDepot[i] == day.calcFinishDepot[i] )
                for v in range(len(day.Vehicles)):
                    vehicle = day.Vehicles[v]
                    self._checkError('Incorrect distance for vehicle %d of day %d (%d instead of %d).' % (v+1,day.dayNumber,vehicle.givenDistance if vehicle.givenDistance is not None else 0,vehicle.calcDistance), vehicle.givenDistance is None or vehicle.givenDistance == vehicle.calcDistance )
                    if len(vehicle.givenVisits):
                        for V in range(len(vehicle.calcVisits)):
                            for i in range(len(vehicle.calcVisits[V])):
                                self._checkError('Incorrect tool count for tool %d at visit %d for vehicle %d of day %d (%d instead of %d)' % (i+1,v+1,V+1,day.dayNumber,vehicle.givenVisits[V][i],vehicle.calcVisits[V][i]), vehicle.givenVisits[V][i] == vehicle.calcVisits[V][i] )
        except self.BaseParseException as E:
            return (False, E.message if E.message is not None else '')
        except:
            print('Crash during CVRPTWUI solution validation\nThe following errors were found:')
            print( '\t' + '\n\t'.join(self.errorReport) )
            raise
        
        return (True, '')
        
    def writeSolution(self,filename,writeExtra):
        if filename.endswith('.xml'):
            res = self._writeSolutionXML(filename,writeExtra)
        else:
            res = self._writeSolutionTXT(filename,writeExtra)
        if res[0]:
            print('Solution file written to %s' % filename)
        else:
            print('Error writing output file %s: %s' % (filename,res[1]))

    def _writeSolutionTXT(self,filename,writeExtra):
        try:
            fd = open(filename,  mode='w')
        except:
            return (False, 'Could not write to file.')
        
        with fd:
            self._writeAssignment(fd,self.LANG.TXT.dataset,self.Dataset)
            self._writeAssignment(fd,self.LANG.TXT.name,self.Name)
            fd.write('\n')
            if writeExtra:
                self._writeAssignment(fd,self.LANG.TXT.maxNumVehicles,self.calcCost.MaxNumberOfVehicles)
                self._writeAssignment(fd,self.LANG.TXT.numVehicleDays,self.calcCost.NumberOfVehicleDays)
                self._writeAssignment(fd,self.LANG.TXT.toolUse,' '.join(str(t) for t in self.calcCost.ToolCount))
                self._writeAssignment(fd,self.LANG.TXT.distance,self.calcCost.Distance)
                self._writeAssignment(fd,self.LANG.TXT.cost,self.calcCost.Cost)
                fd.write('\n')
            for day in self.Days:
                self._writeAssignment(fd,self.LANG.TXT.day,day.dayNumber)
                if writeExtra:
                    self._writeAssignment(fd,self.LANG.TXT.nofVehicles,day.GivenNumberOfVehicles)
                    self._writeAssignment(fd,self.LANG.TXT.startDepot,' '.join(str(t) for t in day.calcStartDepot))
                    self._writeAssignment(fd,self.LANG.TXT.finishDepot,' '.join(str(t) for t in day.calcFinishDepot))
                for i in range(len(day.Vehicles)):
                    v = day.Vehicles[i]
                    line = [i+1, 'R' ] + v.Route
                    fd.write('\t'.join(str(e) for e in line) + '\n')
                    if writeExtra:
                        for j in range(len(v.calcVisits)):
                            line = [i+1, 'V', j+1 ] + v.calcVisits[j]
                            fd.write('\t'.join(str(e) for e in line) + '\n')
                        fd.write('%d\tD\t%d\n' % (i+1, v.calcDistance) )
                fd.write('\n')
                
        return (True, '')
     
    def _writeSolutionXML(self,filename,writeExtra):
        root = ET.Element(self.LANG.XML.solution)
        
        info = ET.SubElement( root, self.LANG.XML.info )
        ET.SubElement( info, self.LANG.XML.dataset ).text = self.Dataset
        ET.SubElement( info, self.LANG.XML.name ).text = self.Name
        
        if writeExtra:
            cost = ET.SubElement( root, self.LANG.XML.cost )
            ET.SubElement( cost, self.LANG.XML.maxNumVehicles ).text = str(self.calcCost.MaxNumberOfVehicles)
            ET.SubElement( cost, self.LANG.XML.numVehicleDays ).text = str(self.calcCost.NumberOfVehicleDays)
            ET.SubElement( cost, self.LANG.XML.distance ).text = str(self.calcCost.Distance)
            ET.SubElement( cost, self.LANG.XML.costValue ).text = str(self.calcCost.Cost)
            tools = ET.SubElement( cost, self.LANG.XML.tools )
            for t in range(len(self.calcCost.ToolCount)):
                ET.SubElement( tools, self.LANG.XML.tool, {self.LANG.XML.attr_id: str(t+1)} ).text = str(self.calcCost.ToolCount[t])
        
        days = ET.SubElement( root, self.LANG.XML.days )
        for day in self.Days:
            dayTag = ET.SubElement( days, self.LANG.XML.day, {self.LANG.XML.attr_id: str(day.dayNumber)} )
            if writeExtra:
                startDepot = ET.SubElement( dayTag, self.LANG.XML.startDepot )
                finishDepot = ET.SubElement( dayTag, self.LANG.XML.finishDepot )
                for t in range(len(day.calcStartDepot)):
                    ET.SubElement( startDepot, self.LANG.XML.tool, {self.LANG.XML.attr_id: str(t+1)} ).text = str(day.calcStartDepot[t])
                    ET.SubElement( finishDepot, self.LANG.XML.tool, {self.LANG.XML.attr_id: str(t+1)} ).text = str(day.calcFinishDepot[t])

            vehicles = ET.SubElement( dayTag, self.LANG.XML.vehicles )    
            if writeExtra:
                vehicles.attrib[self.LANG.XML.attr_nofVehicles] = str(len(day.Vehicles))
            for v in range(len(day.Vehicles)):
                vehicle = day.Vehicles[v]
                vehicleTag = ET.SubElement( vehicles, self.LANG.XML.vehicle, {self.LANG.XML.attr_id: str(v+1)} )
                if writeExtra:
                    ET.SubElement( vehicleTag, self.LANG.XML.distance ).text = str(vehicle.calcDistance)
                route = ET.SubElement( vehicleTag, self.LANG.XML.route )
                visit = 0;
                for n in vehicle.Route:
                    if n == 0:
                        depot = ET.SubElement( route, self.LANG.XML.depot )
                        if writeExtra:
                            for t in range(len(vehicle.calcVisits[visit])):
                                ET.SubElement( depot, self.LANG.XML.tool, {self.LANG.XML.attr_id: str(t+1)} ).text = str(vehicle.calcVisits[visit][t])                            
                        visit += 1
                    else:
                        ET.SubElement( route, self.LANG.XML.request, {self.LANG.XML.attr_type: self.LANG.XML.pickup if n < 0 else self.LANG.XML.deliver} ).text = str(abs(n))
                    
        SolutionCVRPTWUI.indent(root)
        
        try:
            fd = open(filename,  mode='w')
        except:
            return (False, 'Could not write to file.')
        
        with fd:
            tree = ET.ElementTree(root)
            fd.write('<?xml version="1.0" encoding="UTF-8"?>\n')
            tree.write(fd,encoding='utf-8',)
            
        return (True, '')



def DoWork(args):
    instance = args.instance
    if not instance:
        if args.solution.endswith('.sol.xml'):
            instance = args.solution[:-7] + 'xml'
            print('No instance file specified, trying: %s' % instance)
        elif args.solution.endswith('.sol.txt'):
            instance = args.solution[:-7] + 'txt'
            print('No instance file specified, trying: %s ' % instance)
        else:
            print('No instance file specified and unable to determine one based on the solution file')
            return
    
    Instance = InstanceCVRPTWUI(instance,args.itype)
    if not Instance.isValid():
        print('File %s is an invalid CVRPTWUI instance file\nIt contains the following errors:' % instance)
        print( '\t' + '\n\t'.join(Instance.errorReport) )
        return
    Solution = SolutionCVRPTWUI(args.solution,Instance,args.type,args.continueOnError)   
    if Solution.isValid():
        print('Solution %s is a valid CVRPTWUI solution' % args.solution)
        if not args.skipExtraDataCheck:
            res = Solution.areGivenValuesValid()
            if res[0]:
                print('The given solution information is correct')
            else:
                print(res[1])
        print('\t' + '\n\t'.join(str(Solution.calcCost).split('\n')))
        if args.outputFile:
            Solution.writeSolution(args.outputFile,args.writeExtra)
        if len(Solution.warningReport) > 0:
            print('There were warnings:')
            print( '\t' + '\n\t'.join(Solution.warningReport) )
    else:
        print('File %s is an invalid CVRPTWUI solution file\nIt contains the following errors:' % args.solution)
        print( '\t' + '\n\t'.join(Solution.errorReport) )
        if len(Solution.warningReport) > 0:
            print('There were also warnings:')
            print( '\t' + '\n\t'.join(Solution.warningReport) )



if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Read and checks CVRPTWUI solution file.')
    parser.add_argument('--solution', '-s', metavar='SOLUTION_FILE', required=True,
                        help='The solution file')
    parser.add_argument('--instance', '-i', metavar='INSTANCE_FILE',
                        help='The instance file')
    parser.add_argument('--type', '-t', choices=['txt', 'xml'],
                        help='Solution file type')
    parser.add_argument('--itype', choices=['txt', 'xml'],
                        help='instance file type')
    parser.add_argument('--outputFile', '-o', metavar='NEW_SOLUTION_FILE',
                        help='Write the solution to this file')
    parser.add_argument('--writeExtra', '-e', action='store_true',
                        help='Write the extra data in the outputfile')
    parser.add_argument('--skipExtraDataCheck', '-S', action='store_true',
                        help='Skip extra data check')
    parser.add_argument('--continueOnError', '-C', action='store_true',
                        help='Try to continue after the first error in the solution. This may result in a crash (found errors are reported). Note: Any error after the first may be a result of a previous error')
    args = parser.parse_args()
    
    if args.writeExtra and not args.outputFile:
        parser.error('--writeExtra can only be given when --outputFile is also given')

    DoWork(args)
    
            
    
    