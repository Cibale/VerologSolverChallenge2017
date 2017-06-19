#! /usr/bin/env python

import argparse
import xml.etree.ElementTree as ET
import math
import baseCVRPTWUI as base
from pprint import pprint as pprint

class InstanceCVRPTWUI(base.BaseParser):
    parsertype = 'instance'
    
    class LANG:
        class TXT:
            dataset = 'DATASET'
            name = 'NAME'
            days = 'DAYS'
            capacity = 'CAPACITY'
            maxTripDistance = 'MAX_TRIP_DISTANCE'
            depot = 'DEPOT_COORDINATE'
            vehicleCost = 'VEHICLE_COST'
            vehicleDayCost = 'VEHICLE_DAY_COST'
            distanceCost = 'DISTANCE_COST'
            tools = 'TOOLS'
            coordinates = 'COORDINATES'
            requests = 'REQUESTS'
            distance = 'DISTANCE'
        class XML:
            instance = 'instance'
            attr_days = 'number_of_days'
            info = 'info'
            dataset = 'dataset'
            name = 'name'
            network = 'network'
            nodes = 'nodes'
            node = 'node'
            attr_id = 'id'
            attr_type = 'type'
            cx = 'cx'
            cy = 'cy'
            euclidean = 'euclidean'
            floor = 'floor'
            links = 'links'
            link = 'link'
            attr_symmetric = 'symmetric'
            attr_head = 'head'
            attr_tail = 'tail'
            length = 'length'
            fleet = 'fleet'
            vehicleProfile = 'vehicle_profile'
            departureNode = 'departure_node'
            arrivalNode = 'arrival_node'
            capacity = 'capacity'
            maxTravelDistance = 'max_travel_distance'
            requests = 'requests'
            request = 'request'
            attr_node = 'node'
            quantity = 'quantity'
            resource = 'resource'
            custom = 'custom'
            firstDeliverDay = 'first_deliver_day'
            lastDeliverDay = 'last_deliver_day'
            daysNeeded = 'days_needed'
            resources = 'resources'
            resource = 'resource'
            attr_renewable = 'renewable'
            attr_size = 'size'
            attr_cost = 'cost'
            vehicleCost = 'fix_cost'
            vehicleDayCost = 'cost_x_time'
            distanceCost = 'cost_x_distance'
    
    class Tool(object):
        def __init__(self,ID,weight,amount,cost):
            self.ID = ID
            self.weight = weight
            self.amount = amount
            self.cost = cost
        def __repr__(self):
            return '%d\t%d\t%d\t%d' % (self.ID,self.weight,self.amount, self.cost)
    
    class Request(object):
        def __init__(self,ID,node,fromDay,toDay,numDays,tool,toolCount):
            self.ID = ID
            self.node = node
            self.fromDay = fromDay
            self.toDay = toDay
            self.numDays = numDays
            self.tool = tool
            self.toolCount = toolCount
        def __repr__(self):
            return '%d\t%d\t%d\t%d\t%d\t%d\t%d' % (self.ID,self.node,self.fromDay,self.toDay,self.numDays,self.tool,self.toolCount)
    
    class Coordinate(object):
        def __init__(self,ID,X,Y):
            self.ID = ID
            self.X = X
            self.Y = Y
        def __repr__(self):
            return '%d\t%d\t%d' % (self.ID,self.X,self.Y)
    
    def __init__(self, inputfile=None,filetype=None,continueOnErr=False):
        if inputfile is not None:
            self._doinit(inputfile,filetype,continueOnErr)
        else:
            self._initData()
        
    def _initData(self):
        self.Tools = []
        self.Requests = []
        self.Coordinates = []
        self.ReadDistance = None
        self.calcDistance = None
    
    def _initTXT(self):
        try:
            fd = open(self.inputfile, 'r')
        except:
            self.errorReport.append( 'Instance file %s could not be read.' % self.inputfile )
            return
        
        try:
            with fd:
                self.Dataset = self._checkAssignment(fd,self.LANG.TXT.dataset,'string')
                self.Name = self._checkAssignment(fd,self.LANG.TXT.name,'string')
                
                self.Days = self._checkInt( 'Days', self._checkAssignment(fd,self.LANG.TXT.days) )
                self.Capacity = self._checkInt( 'Capacity', self._checkAssignment(fd,self.LANG.TXT.capacity) )
                self.MaxDistance = self._checkInt( 'Max trip distance', self._checkAssignment(fd,self.LANG.TXT.maxTripDistance) )
                self.DepotCoordinate = self._checkInt( 'Depot', self._checkAssignment(fd,self.LANG.TXT.depot) )
                
                self.VehicleCost = self._checkInt( 'Vehicle Cost', self._checkAssignment(fd,self.LANG.TXT.vehicleCost) )
                self.VehicleDayCost = self._checkInt( 'Vehicle Day Cost', self._checkAssignment(fd,self.LANG.TXT.vehicleDayCost) )
                self.DistanceCost = self._checkInt( 'Distance Cost', self._checkAssignment(fd,self.LANG.TXT.distanceCost) )
                
                Num_tools = self._checkInt("Number of tools", self._checkAssignment(fd,self.LANG.TXT.tools))
                for i in range(Num_tools):
                    line = self._getNextLine(fd)
                    ToolsLine = line.split()
                    self._checkError("Expected four integers on a tools line. Found: '%s'." % line,
                                    len(ToolsLine) == 4)
                    toolID = self._checkInt('Tool ID', ToolsLine[0] )
                    weight = self._checkInt('Tool weight', ToolsLine[1], 'for tool %d ' % toolID )
                    amount = self._checkInt('Tool amount', ToolsLine[2], 'for tool %d ' % toolID )
                    cost = self._checkInt('Tool cost', ToolsLine[3], 'for tool %d ' % toolID )
                    self.Tools.append( self.Tool(toolID,weight,amount,cost) )
                    self._checkError('The indexing of the Tools is incorrect at Tool nr. %d.' % toolID, toolID == len(self.Tools) )
                    
                Num_coordinates = self._checkInt("Number of coordinates", self._checkAssignment(fd,self.LANG.TXT.coordinates))
                self._checkError('Depot (%s) is not a valid coordinate', 0 <= self.DepotCoordinate < Num_coordinates )
                for i in range(Num_coordinates):
                    line = self._getNextLine(fd)
                    CoordinateLine = line.split()
                    self._checkError("Expected three integers on a coordinate line. Found: '%s'." % line,
                                    len(CoordinateLine) == 3)
                    locID = self._checkInt('Coordinate ID', CoordinateLine[0] )
                    self._checkError('The indexing of the Coordinates is incorrect at Coordinate nr. %d.' % locID, locID == len(self.Coordinates) )
                    X = self._checkInt('Coordinate X', CoordinateLine[1], 'for Coordinate %d ' % locID )
                    Y = self._checkInt('Coordinate Y', CoordinateLine[2], 'for Coordinate %d ' % locID )
                    self.Coordinates.append( self.Coordinate(locID,X,Y) )
                
                Num_requests = self._checkInt("Number of requests", self._checkAssignment(fd,self.LANG.TXT.requests))
                for i in range(Num_requests):
                    line = self._getNextLine(fd)
                    RequestLine = line.split()
                    self._checkError("Expected seven integers on a request line. Found: '%s'." % line,
                                    len(RequestLine) == 7)
                    requestID = self._checkInt('Request ID', RequestLine[0] )
                    node = self._checkInt('Request node', RequestLine[1], 'for Request %d ' % requestID )
                    self._checkError('Request node %d is larger then the number of coordinates (%d) for request %d' % (node, self.Days, requestID), 0 <= node < Num_coordinates )
                    fromDay = self._checkInt('Request from-day', RequestLine[2], 'for Request %d ' % requestID )
                    self._checkError('Request from-day %d is larger then the horizon (%d) for request %d' % (fromDay, self.Days, requestID), 0 < fromDay <= self.Days )
                    toDay = self._checkInt('Request to-day', RequestLine[3], 'for Request %d ' % requestID )
                    self._checkError('Request to-day %d is larger then the horizon (%d) for request %d' % (toDay, self.Days, requestID), 0 < toDay <= self.Days )
                    numDays = self._checkInt('Request number of days', RequestLine[4], 'for Request %d ' % requestID )
                    self._checkError('Request last pickup day %d is larger then the horizon (%d) for request %d' % (toDay+numDays, self.Days, requestID), toDay+numDays <= self.Days )
                    self._checkError('Request number of days is not strict positive (%d) for request %d' % (numDays, requestID), 0 < numDays )
                    tool = self._checkInt('Request tool', RequestLine[5], 'for Request %d ' % requestID )
                    self._checkError('Request tool %d is larger then the number of tools (%d) for request %d' % (tool, Num_tools, requestID), 0 < tool <= Num_tools )
                    toolCount = self._checkInt('Request tool count', RequestLine[6], 'for Request %d ' % requestID )
                    self._checkError('Request tool count %d is larger then the number of available tools (%d) for request %d' % (toolCount, self.Tools[tool-1].amount, requestID), toolCount <= self.Tools[tool-1].amount )
                    self._checkError('Request number of tools is not strict positive (%d) for request %d' % (toolCount, requestID), 0 < toolCount )
                    self.Requests.append( self.Request(requestID,node,fromDay,toDay,numDays,tool,toolCount) )
                    self._checkError('The indexing of the Requests is incorrect at Request nr. %d.' % requestID, requestID == len(self.Requests) )
                    
                line = self._getNextLine(fd)
                if line == self.LANG.TXT.distance:
                    self.ReadDistance = []
                    for i in range(Num_coordinates):
                        line = self._getNextLine(fd)
                        distLine = line.split()
                        self._checkError('Expected %d integers on a distance line. Found %d: %s.' % (Num_coordinates,len(distLine),line),
                                        len(distLine) == Num_coordinates)

                        try:
                            dists = [int(x) for x in distLine]
                        except ValueError as err:
                            field = err.message.split(':',1)[1].strip().strip("'")
                            self._checkError('Expected %d integers on a distance line. Found incorrect data (%s): %s.' % (Num_coordinates,field,line),
                                              False)
                        except:
                            self._checkError('Expected %d integers on a distance line. Found incorrect data: %s.' % (Num_coordinates,line),
                                              False)
                        
                        self.ReadDistance.append(dists)
                
        except self.BaseParseException:
            pass
        except:
            print('Crash during CVRPTWUI instance reading\nThe following errors were found:')
            print( '\t' + '\n\t'.join(self.errorReport) )
            raise
            
    def _initXML(self):
        try:
            fd = open(self.inputfile, 'r')
        except:
            self.errorReport.append( 'Instance file %s could not be read.' % self.inputfile )
            return
        
        try:
            with fd:
                tree = ET.parse(fd)
                root = tree.getroot()                
                self._checkError('Root tag is not equal to instance.',root.tag == self.LANG.XML.instance)
                
                info = self._findTag(root, self.LANG.XML.info )
                self.Dataset = self._findTag(info, self.LANG.XML.dataset ).text
                self.Name = self._findTag(info, self.LANG.XML.name ).text
                
                self.Days = self._checkInt('Number of Days', self._findAttribute(root, self.LANG.XML.attr_days) )
                
                profile = self._findTag(self._findTag(root, self.LANG.XML.fleet ), self.LANG.XML.vehicleProfile )
                
                self.Capacity = self._checkInt('Capacity', self._findTag(profile, self.LANG.XML.capacity ).text )
                self.MaxDistance = self._checkInt( 'Max trip distance', self._findTag(profile, self.LANG.XML.maxTravelDistance ).text )
                                
                self.DepotCoordinate = self._checkInt( self.LANG.XML.departureNode, self._findTag(profile, self.LANG.XML.departureNode ).text )
                arrDepot = self._checkInt( self.LANG.XML.arrivalNode, self._findTag(profile, self.LANG.XML.arrivalNode ).text )
                self._checkError('Departure node is not equal to the arrival node', self.DepotCoordinate == arrDepot )
                
                self.VehicleCost = self._checkInt( 'Vehicle Cost', self._findTag(profile, self.LANG.XML.vehicleCost ).text )
                self.VehicleDayCost = self._checkInt( 'Vehicle Day Cost', self._findTag(profile, self.LANG.XML.distanceCost ).text )
                self.DistanceCost = self._checkInt( 'Distance Cost', self._findTag(profile, self.LANG.XML.vehicleDayCost ).text )
                                
                resources = self._findTag(root, self.LANG.XML.resources )
                Num_tools = 0
                for resource in resources.findall(self.LANG.XML.resource):
                    Num_tools += 1
                    toolID = self._checkInt('Tool ID', self._findAttribute(resource, self.LANG.XML.attr_id) )
                    self._checkError('The indexing of the Tools is incorrect at Tool nr. %d.' % toolID, toolID == Num_tools )
                    weight = self._checkInt('Tool size', self._findAttribute(resource, self.LANG.XML.attr_size, ' (tool id=%d)' % toolID), 'for tool %d ' % toolID )
                    amount = self._checkInt('Tool amount', resource.text, 'for tool %d ' % toolID )
                    cost = self._checkInt('Tool cost', self._findAttribute(resource, self.LANG.XML.attr_cost, ' (tool id=%d)' % toolID), 'for tool %d ' % toolID )
                    self.Tools.append( self.Tool(toolID,weight,amount,cost) )
                
                network = self._findTag(root, self.LANG.XML.network )
                nodes = self._findTag(network, self.LANG.XML.nodes )
                self._findTag(network, self.LANG.XML.euclidean )
                self._findTag(network, self.LANG.XML.floor )
                
                Num_coordinates = 0
                for node in nodes.findall(self.LANG.XML.node):
                    locID = self._checkInt('Node ID', self._findAttribute(node, self.LANG.XML.attr_id) )
                    self._checkError('The indexing of the Coordinates is incorrect at Coordinate nr. %d.' % locID, locID == Num_coordinates )
                    nodeType = self._checkInt('Node type', self._findAttribute(node, self.LANG.XML.attr_type, ' (node id=%d)' % locID) )
                    self._checkError('Incorrect node type (%d) for node %d, expected %d' % (nodeType,locID,1 if self.DepotCoordinate == locID else 0), (self.DepotCoordinate == locID and nodeType == 0) or (self.DepotCoordinate != locID and nodeType == 1) )
                    X = self._checkInt('Coordinate X', self._findTag(node, self.LANG.XML.cx ).text, 'for Node %d ' % locID )
                    Y = self._checkInt('Coordinate Y', self._findTag(node, self.LANG.XML.cy ).text, 'for Node %d ' % locID )
                    self.Coordinates.append( self.Coordinate(locID,X,Y) )
                    Num_coordinates += 1
                self._checkError('Depot (%s) is not a valid coordinate', 0 <= self.DepotCoordinate < Num_coordinates )
                
                requests = self._findTag(root, self.LANG.XML.requests )
                
                Num_requests = 0
                for request in requests.findall(self.LANG.XML.request):
                    Num_requests += 1
                    requestID = self._checkInt('Request ID', self._findAttribute(request, self.LANG.XML.attr_id) )
                    self._checkError('The indexing of the Requests is incorrect at Request nr. %d.' % requestID, requestID == Num_requests )
                    node = self._checkInt('Request node', self._findAttribute(request, self.LANG.XML.attr_node, ' (request id=%d)' % requestID), 'for Request %d ' % requestID )
                    self._checkError('Request node %d is larger then the number of coordinates (%d) for request %d' % (node, Num_coordinates, requestID), 0 <= node < Num_coordinates )
                    custom = self._findTag(request, self.LANG.XML.custom, ' (id=%d)' % requestID )
                    fromDay = self._checkInt('Request from-day', self._findTag(custom, self.LANG.XML.firstDeliverDay, ' (request id=%d)' % requestID ).text, 'for Request %d ' % requestID )
                    self._checkError('Request from-day %d is larger then the horizon (%d) for request %d' % (fromDay, self.Days, requestID), 0 < fromDay <= self.Days )
                    toDay = self._checkInt('Request to-day', self._findTag(custom, self.LANG.XML.lastDeliverDay, ' (request id=%d)' % requestID ).text, 'for Request %d ' % requestID )
                    self._checkError('Request to-day %d is larger then the horizon (%d) for request %d' % (toDay, self.Days, requestID), 0 < toDay <= self.Days )
                    numDays = self._checkInt('Request number of days', self._findTag(custom, self.LANG.XML.daysNeeded, ' (request id=%d)' % requestID ).text, 'for Request %d ' % requestID )
                    self._checkError('Request last pickup day %d is larger then the horizon (%d) for request %d' % (toDay+numDays, self.Days, requestID), toDay+numDays <= self.Days )
                    self._checkError('Request number of days is not strict positive (%d) for request %s' % (numDays, requestID), 0 < numDays )
                    resource = self._findTag(request, self.LANG.XML.resource, ' (request id=%d)' % requestID )
                    tool = self._checkInt('Request tool', self._findAttribute(resource, self.LANG.XML.attr_id, ' (request id=%d)' % requestID), 'for Request %d ' % requestID )
                    self._checkError('Request tool %d is larger then the number of tools (%d) for request %s' % (tool, Num_tools, requestID), 0 < tool <= Num_tools )
                    toolCount = self._checkInt('Request tool count', resource.text, 'for Request %d ' % requestID )
                    self._checkError('Request tool count %d is larger then the number of available tools (%d) for request %s' % (toolCount, self.Tools[tool-1].amount, requestID), toolCount <= self.Tools[tool-1].amount )
                    self._checkError('Request number of tools is not strict positive (%d) for request %s' % (toolCount, requestID), 0 < toolCount )
                    quantity = self._checkInt('Request quantity', self._findTag(request, self.LANG.XML.quantity ).text, 'for Request %d ' % requestID )
                    self._checkError('Incorrect quantity (%d) for request %d, expected %d' % (quantity,requestID,toolCount * self.Tools[tool-1].weight), quantity == toolCount * self.Tools[tool-1].weight )
                    self.Requests.append( self.Request(requestID,node,fromDay,toDay,numDays,tool,toolCount) )
                
                links = network.find( self.LANG.XML.links )
                if links is not None:
                    self._checkError( 'The links are not given as symmetric', self._findAttribute(links, self.LANG.XML.attr_symmetric ) == 'true' )
                    self.ReadDistance = [[None for x in range(Num_coordinates)] for x in range(Num_coordinates)]
                    for i in range(Num_coordinates):
                        self.ReadDistance[i][i] = 0
                    for link in links.findall( self.LANG.XML.link ):
                        head = self._checkInt('Link head', self._findAttribute(link, self.LANG.XML.attr_head) )
                        tail = self._checkInt('Link tail', self._findAttribute(link, self.LANG.XML.attr_tail) )
                        length = self._checkInt('Link length', self._findTag(link, self.LANG.XML.length).text )
                        self._checkError( 'Link head and tail should be different, not equal (%d).' % head, head != tail )
                        self._checkError( 'Link head (%d) is an incorrect coordinate.' % head, 0 <= head < Num_coordinates )
                        self._checkError( 'Link tail (%d) is an incorrect coordinate.' % tail, 0 <= tail < Num_coordinates )
                        self._checkError( 'Head (%d) and tail (%d) combination, or vice versa, is encountered twice.' % (head,tail), self.ReadDistance[head][tail] == None )
                        self.ReadDistance[head][tail] = self.ReadDistance[tail][head] = length
                    for i in range(Num_coordinates):
                        for j in range(i,Num_coordinates):
                            self._checkError( 'Head (%d) and tail (%d) combination, or vice versa, is not encountered.' % (i,j), self.ReadDistance[i][j] != None )                        
                                
        except self.BaseParseException:
            pass
        except:
            print('Crash during CVRPTWUI instance reading\nThe following errors were found:')
            print( '\t' + '\n\t'.join(self.errorReport) )
            raise
    
    def calculateDistances(self):
        if not self.isValid() or self.calcDistance is not None:
            return
        numLocs = len(self.Coordinates)
        self.calcDistance = [[0 for x in range(numLocs)] for x in range(numLocs)]
        for i in range(numLocs): 
            cI = self.Coordinates[i]
            for j in range(i,numLocs):
                cJ = self.Coordinates[j]
                dist = math.floor( math.sqrt( pow(cI.X-cJ.X,2) + pow(cI.Y-cJ.Y,2) ) )
                self.calcDistance[i][j] = self.calcDistance[j][i] = int(dist)
                
    def isValid(self):
        return not self.errorReport
        
    def areDistancesValid(self):
        if self.ReadDistance is None:
            return (True,'Distances are not given.')
        self.calculateDistances()
        if self.ReadDistance != self.calcDistance:
            numLocs = len(self.Coordinates)
            for i in range(numLocs): 
                for j in range(numLocs):
                    if self.ReadDistance[i][j] != self.calcDistance[i][j]:
                        return (False,'Incorrect Distances. First difference is at location %d,%d: %d should be %d' % (i,j,self.ReadDistance[i][j],self.calcDistance[i][j])  )
        return (True,'The given distances are correct')
        
    def writeInstance(self,filename,writeMatrix):
        if filename.endswith('.xml'):
            res = self._writeInstanceXML(filename,writeMatrix)
        else:
            res = self._writeInstanceTXT(filename,writeMatrix)
        if res[0]:
            print('Instance file written to %s' % filename)
        else:
            print('Error writing output file %s: %s' % (filename,res[1]))

    def _writeInstanceTXT(self,filename,writeMatrix):
        try:
            fd = open(filename,  mode='w')
        except:
            return (False, 'Could not write to file.')
        
        with fd:
            self._writeAssignment(fd,self.LANG.TXT.dataset,self.Dataset)
            self._writeAssignment(fd,self.LANG.TXT.name,self.Name)
            fd.write('\n')
            self._writeAssignment(fd,self.LANG.TXT.days,self.Days)
            self._writeAssignment(fd,self.LANG.TXT.capacity,self.Capacity)
            self._writeAssignment(fd,self.LANG.TXT.maxTripDistance,self.MaxDistance)
            self._writeAssignment(fd,self.LANG.TXT.depot,self.DepotCoordinate)
            fd.write('\n')
            self._writeAssignment(fd,self.LANG.TXT.vehicleCost,self.VehicleCost)
            self._writeAssignment(fd,self.LANG.TXT.vehicleDayCost,self.VehicleDayCost)
            self._writeAssignment(fd,self.LANG.TXT.distanceCost,self.DistanceCost)
            fd.write('\n')
            
            self._writeAssignment(fd,self.LANG.TXT.tools,len(self.Tools))
            for i in range(len(self.Tools)):
                fd.write('%s\n' % str(self.Tools[i]) )
            fd.write('\n')
            
            self._writeAssignment(fd,self.LANG.TXT.coordinates,len(self.Coordinates))
            for i in range(len(self.Coordinates)):
                fd.write('%s\n' % str(self.Coordinates[i]) )
            fd.write('\n')
            
            self._writeAssignment(fd,self.LANG.TXT.requests,len(self.Requests))
            for i in range(len(self.Requests)):
                fd.write('%s\n' % str(self.Requests[i]) )
            fd.write('\n')
            
            if writeMatrix:
                self.calculateDistances()
                fd.write(self.LANG.TXT.distance + '\n')
                for distLine in self.calcDistance:
                    fd.write('\t'.join(str(d) for d in distLine) + '\n')
                fd.write('\n')
            
        return (True, '')
    
    def _writeInstanceXML(self,filename,writeMatrix):
        root = ET.Element(self.LANG.XML.instance)
        root.attrib[self.LANG.XML.attr_days] = str(self.Days)
        
        info = ET.SubElement( root, self.LANG.XML.info )
        ET.SubElement( info, self.LANG.XML.dataset ).text = self.Dataset
        ET.SubElement( info, self.LANG.XML.name ).text = self.Name
        
        network = ET.SubElement( root, self.LANG.XML.network )
        nodes = ET.SubElement( network, self.LANG.XML.nodes )
        ET.SubElement( network, self.LANG.XML.euclidean )
        ET.SubElement( network, self.LANG.XML.floor )
        for i in range(len(self.Coordinates)):
            coord = self.Coordinates[i]
            node = ET.SubElement( nodes, self.LANG.XML.node, {self.LANG.XML.attr_id: str(coord.ID), self.LANG.XML.attr_type: '0' if i == self.DepotCoordinate else '1' } )
            ET.SubElement( node, self.LANG.XML.cx ).text = str(coord.X)
            ET.SubElement( node, self.LANG.XML.cy ).text = str(coord.Y)
            
        if writeMatrix:
            links = ET.SubElement( network, self.LANG.XML.links, {self.LANG.XML.attr_symmetric: 'true'} )
            for i in range(len(self.calcDistance)): 
                for j in range(i+1,len(self.calcDistance)):
                    ET.SubElement( ET.SubElement( links, self.LANG.XML.link, { self.LANG.XML.attr_head: str(i), self.LANG.XML.attr_tail: str(j) } ), self.LANG.XML.length ).text = str(self.calcDistance[i][j])
        
        vehicleProfile = ET.SubElement( ET.SubElement( root, self.LANG.XML.fleet ), self.LANG.XML.vehicleProfile, {self.LANG.XML.attr_type: '1' } )
        ET.SubElement( vehicleProfile, self.LANG.XML.departureNode ).text = str(self.DepotCoordinate)
        ET.SubElement( vehicleProfile, self.LANG.XML.arrivalNode ).text = str(self.DepotCoordinate)
        ET.SubElement( vehicleProfile, self.LANG.XML.capacity ).text = str(self.Capacity)
        ET.SubElement( vehicleProfile, self.LANG.XML.maxTravelDistance ).text = str(self.MaxDistance)
        ET.SubElement( vehicleProfile, self.LANG.XML.vehicleCost ).text = str(self.VehicleCost)
        ET.SubElement( vehicleProfile, self.LANG.XML.distanceCost ).text = str(self.DistanceCost)
        ET.SubElement( vehicleProfile, self.LANG.XML.vehicleDayCost ).text = str(self.VehicleDayCost)
        
        requests = ET.SubElement( root, self.LANG.XML.requests )
        for i in range(len(self.Requests)):
            req = self.Requests[i]
            request = ET.SubElement( requests, self.LANG.XML.request, {self.LANG.XML.attr_id: str(req.ID), self.LANG.XML.node: str(req.node) } )
            ET.SubElement( request, self.LANG.XML.quantity ).text = str(req.toolCount * self.Tools[req.tool-1].weight)
            ET.SubElement( request, self.LANG.XML.resource, {self.LANG.XML.attr_id: str(req.tool) } ).text = str(req.toolCount)
            custom = ET.SubElement( request, self.LANG.XML.custom )
            ET.SubElement( custom, self.LANG.XML.firstDeliverDay ).text = str(req.fromDay)
            ET.SubElement( custom, self.LANG.XML.lastDeliverDay ).text = str(req.toDay)
            ET.SubElement( custom, self.LANG.XML.daysNeeded ).text = str(req.numDays)
            
        resources = ET.SubElement( root, self.LANG.XML.resources )
        for i in range(len(self.Tools)):
            tool = self.Tools[i]
            ET.SubElement( resources, self.LANG.XML.resource, {self.LANG.XML.attr_id: str(tool.ID), self.LANG.XML.attr_renewable: 'false', self.LANG.XML.attr_size: str(tool.weight), self.LANG.XML.attr_cost: str(tool.cost) } ).text = str(tool.amount)
        
        InstanceCVRPTWUI.indent(root)
        
        try:
            fd = open(filename,  mode='w')
        except:
            return (False, 'Could not write to file.')
        
        with fd:
            tree = ET.ElementTree(root)
            fd.write('<?xml version="1.0" encoding="UTF-8"?>\n')
            tree.write(fd,encoding='utf-8',)

        
        return (True, '')

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Read and checks CVRPTWUI instance file.')
    parser.add_argument('--instance', '-i', metavar='INSTANCE_FILE', required=True,
                        help='The instance file')
    parser.add_argument('--type', '-t', choices=['txt', 'xml'],
                        help='Instance file type')
    parser.add_argument('--skipDistanceCheck', '-S', action='store_true',
                        help='Skip check on given distances')
    parser.add_argument('--outputFile', '-o', metavar='NEW_INSTANCE_FILE',
                        help='Write the instance to this file')
    parser.add_argument('--writeMatrix', '-m', action='store_true',
                        help='Write the matrix in the outputfile')
    parser.add_argument('--continueOnError', '-C', action='store_true',
                        help='Try to continue after the first error in the solution. This may result in a crash (found errors are reported). Note: Any error after the first may be a result of a previous error')
    args = parser.parse_args()
    
    if args.writeMatrix and not args.outputFile:
        parser.error('--writeMatrix can only be given when --outputFile is also given')
    
    Instance = InstanceCVRPTWUI(args.instance,args.type,args.continueOnError)
    if Instance.isValid():
        print('Instance %s is a valid CVRPTWUI instance' % args.instance)
        if not args.skipDistanceCheck:
            res = Instance.areDistancesValid()
            print(res[1])
        if args.outputFile:
            Instance.writeInstance(args.outputFile,args.writeMatrix)
        if len(Instance.warningReport) > 0:
            print('There were warnings:')
            print( '\t' + '\n\t'.join(Instance.warningReport) )
    else:
        print('File %s is an invalid CVRPTWUI instance file\nIt contains the following errors:' % args.instance)
        print( '\t' + '\n\t'.join(Instance.errorReport) )
        if len(Instance.warningReport) > 0:
            print('There were also warnings:')
            print( '\t' + '\n\t'.join(Instance.warningReport) )

    