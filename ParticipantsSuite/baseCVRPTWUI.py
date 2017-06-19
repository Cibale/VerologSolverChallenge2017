

class BaseParser(object):
    
    class BaseParseException(Exception):
        def __init__(self,message = None):
            self.message = message
            
    def _initType(self,filetype):
        self.type = 'txt'
        if not filetype:
            if self.inputfile.endswith('.xml'):
                self.type = 'xml'
            elif not self.inputfile.endswith('.txt'):
                self.warningReport.append( "Unknown %s file type, assuming 'txt'." % self.parsertype )
        else:
            if filetype != 'txt' and filetype != 'xml':
                self.warningReport.append( "Unknown %s file type: '%s', assuming txt'." % (self.parsertype,filetype) )
            elif filetype == 'xml':
                self.type = 'xml'
    
    def _doinit(self, inputfile,filetype, continueOnErr = False):
        self.errorReport = []
        self.warningReport = []
        self.inputfile = inputfile
        self.breakOnError =  not continueOnErr
        
        if not self.inputfile:
            self.errorReport.append( 'No input file is given.' )
            return
            
        self._initType(filetype)
        self._initData()
        
        if self.type == 'txt':
            self._initTXT()
        elif self.type == 'xml':
            self._initXML()
        else:
            assert False, 'INTERNAL ERROR: INCORRECT FILE TYPE!'
            
    @staticmethod
    def _getNextLine(fd):
        line = '\n'
        while line and not line.strip():
            line = fd.readline()
        return line.strip()
        
    def _checkError(self,message,test):
        if not test:
            self.errorReport.append( message )
            if self.breakOnError:
                raise self.BaseParseException(message)
    
    def _checkInt(self,field,intstr,extra=''):
        try:
            return int(intstr)
        except:
            message = '%s (%s) %sis not an integer.' % (field,intstr,extra)
            self._checkError(message,False)
    
    def _isAssignment(self,fd):
        line = self._getNextLine(fd)
        splitLine = line.split(None,2)
        if len(splitLine) == 3 and splitLine[1] == '=':
            return (splitLine[0], splitLine[2])
        elif not line:
            return None
        else:
            return (None, line)
        
    def _checkAssignment(self,fd,key,fieldtype='number'):
        line = self._getNextLine(fd)
        splitLine = line.split(None,2)
        errorFormat = "Expected header line for locations is of the form '%s = %s'. Found: '%%s'." % (key,fieldtype)
        self._checkError(errorFormat % line, 
                        len(splitLine) == 3 and splitLine[0] == key and splitLine[1] == '=')
        return splitLine[2]
        
    def _findTag(self,elem,match,extra=''):
        res = elem.find(match)
        self._checkError("No '%s' tag found under '%s' tag%s" % (match, elem.tag,extra), res is not None )
        return res
    
    def _findAttribute(self,elem,attribute,extra=''):
        self._checkError("No '%s' attribute found under '%s' tag%s" % (attribute, elem.tag,extra), attribute in elem.attrib )
        return elem.attrib[attribute]
    
    def _writeAssignment(self,fd,lhs,rhs):
        fd.write('%s = %s\n' % (lhs,str(rhs)))
        
    @staticmethod
    #http://effbot.org/zone/element-lib.htm#prettyprint
    def indent(elem, level=0):
        i = '\n' + level*"\t"
        if len(elem):
            if not elem.text or not elem.text.strip():
                elem.text = i + "\t"
            if not elem.tail or not elem.tail.strip():
                elem.tail = i
            for elem in elem:
                BaseParser.indent(elem, level+1)
            if not elem.tail or not elem.tail.strip():
                elem.tail = i
        else:
            if level and (not elem.tail or not elem.tail.strip()):
                elem.tail = i
                
    def _initFile(self,func):
        try:
            fd = open(self.inputfile, 'r')
        except:
            self.errorReport.append( 'Instance file %s could not be read.' % self.inputfile )
            return
        
        try:
            with fd:
                  func(fd)             
        except self.BaseParseException:
            pass