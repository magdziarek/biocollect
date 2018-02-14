// Groovy Rest Client.
// > cd /Users/sat01a/All/sat01a_git/merged/biocollect-3/scripts/RestClient/gonna_watch
// > export PATH=$PATH:/Users/sat01a/All/j2ee/groovy-2.4.11/bin
// > groovy RestClient.groovy
// To get the example post data, enable debugger at this.save and print the variable or right click and store as global variable.
// variable json string data will be printed in the console
// Use http://jsonformatter.org/ to format the data. (make sure to remove the " " around the string...)


// DONT REMOVE 'TYPE' AND 'NAME' IN THE TEMPLATE
// siteId need to be assinged to activity.siteId and activity.output.data.location
// Othersise, fuctions of clusters or points on map will not work
// Udpate session/cookie before run 'deleteActivity' - if need to

//Global.RECORD_LOG appends the existing created activity. Double check the filename

//Sites will be created first and logged into site log, and then reloaded

// User must have a auth token [ ozatlasproxy.ala.org.au]
// Generating UUID on the device: python -c 'import uuid; print str(uuid.uuid1())'

@Grapes([
        @Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7'),
        @Grab('org.apache.httpcomponents:httpmime:4.5.1'),
        @Grab('org.apache.poi:poi:3.10.1'),
        @Grab(group = 'commons-codec', module = 'commons-codec', version = '1.9'),
        @Grab('org.apache.poi:poi-ooxml:3.10.1')]
)


//import org.apache.poi.xssf.usermodel.XSSFWorkbook
//import static org.apache.poi.ss.usermodel.Cell.*

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFCell
import java.util.Date




import java.nio.file.Paths
import static java.util.UUID.randomUUID
import groovy.json.JsonSlurper

import groovyx.net.http.HTTPBuilder
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.FileBody
import groovyx.net.http.Method
import groovyx.net.http.ContentType



class Globals {
    // IMPORTANT CONFIGURATION
    static DEBUG_AND_VALIDATE = false;
    static PROJECT_ID = "abdd9f05-a757-420b-85a6-3e8ae31c2d4f"

    static USERNAME = "qifeng.bai@csiro.au" // 510
    static AUTH_KEY = "889543c1-14ce-4d2d-a1f2-a43b19924ae8"

    static PROJECT_ACTIVITY_ID = "016eb7f9-6a9c-4526-bdb7-c3c273304c7b"
    static IMAGES_PATH = "images//Varanus_varius//"
    static DATA_TEMPLATE_FILE = "data_template_current_surveys.json"

    static SERVER_URL = "http://devt.ala.org.au:8087/biocollect"
    static SPECIES_URL = "https://biocollect.ala.org.au/search/searchSpecies/${PROJECT_ACTIVITY_ID}?limit=1&hub=ala"
    //static ADD_NEW_ACTIVITY_URL = "/ws/bioactivity/save?pActivityId=${PROJECT_ACTIVITY_ID}"
    static ADD_NEW_ACTIVITY_URL = "/bioActivity/ajaxUpdate?pActivityId=${PROJECT_ACTIVITY_ID}"
    static EDIT_ACTIVITY_URL = "/bioActivity/ajaxUpdate"
    static IMAGE_UPLOAD_URL = 'http://devt.ala.org.au:8087/biocollect/ws/attachment/upload'
    //def IMAGE_UPLOAD_URL = 'https://biocollect.ala.org.au/ws/attachment/upload'
    static SITE_CREATION_URL = '/site/ajaxUpdate'
    static RECORD_LOG = "CBOC_DEV.log"
}


static void main(String[] args) {

    String DATA_FILE = "Wolli - cboc surveys - processed - for loading.xls"
    String DATA_TEMPLATE_FILE = "data_template_CBOC_surveys.json"
    String SITE_LOG_FILE = Globals.PROJECT_ID+".site.log"

    if (args && args[0] == 'prod') {
        println('We are uploading to the production server? Y/N')
        def answer = System.in.newReader().readLine()

        if (!(answer == 'Y' || answer == 'y')) {
            println('Stop!')
            System.exit(0)
        } else {
            println 'Uploading to prod........'
            Globals.DEBUG_AND_VALIDATE = false;
            Globals.USERNAME = 'dlutherau@yahoo.com.au'
            Globals.AUTH_KEY = "9601eeee-3c9b-4f24-b4db-5fea68e13c79"
            Globals.SERVER_URL = 'https://biocollect.ala.org.au'
            Globals.IMAGE_UPLOAD_URL = 'https://biocollect.ala.org.au/ws/attachment/upload'
            SITE_LOG_FILE = Globals.PROJECT_ID + ".prod.site.log"
            Globals.RECORD_LOG = "CBOC_prod.log"
        }
    }

    //Create sites without duplciation
    def SITES = loadSites(SITE_LOG_FILE)
    def activities = loadXsl(DATA_FILE)
    println("Total activities to upload = ${activities?.size()}")
    SITES = createSites(activities, SITES,SITE_LOG_FILE)
    createRecords(activities, SITES, DATA_TEMPLATE_FILE)

    println("Completed..")



}

    String tryFormat(String datetime)
    {
        List<String> formatStrings = Arrays.asList("yyyy-MM-dd'T'HH:mm:ss'Z'", "dd/MM/yyyy","yyyy-MM-dd'T'HH:mm");
        for (String formatString : formatStrings)
        {
            try
            {
                TimeZone tz = TimeZone.getTimeZone("UTC");
                java.text.DateFormat df = new java.text.SimpleDateFormat(formatString);
                // Quoted "Z" to indicate UTC, no timezone offset
                df.setTimeZone(tz);
                return df.format(datetime);
            }
            catch (Exception e) {}
        }

        throw new Exception("Cannot recognize: " + datetime);
    }




    def createRecords(activities, sites, data_template_file){
        //Store all created record ID
        File recordsLog = new File( Globals.RECORD_LOG)

        def existingRecords = []
//        if (recordsLog.exists()){
//            recordsLog.eachLine { String line ->
//                String id = line.reverse().take(36).reverse()
//                existingRecords.push(id)
//            }
//        }
//        for(re in existingRecords){
//            deleteActivity("https://biocollect.ala.org.au/ala/bioActivity", re)
//        }
//        println('Delete completed')
//        System.exit(0)

        println("Loading data_template file")
        String jsonStr = new File(data_template_file).text



        // Loop through the activities
        activities?.eachWithIndex { activityRow, activityIndex ->
            if (activityIndex >=0) { // 183
                //String existingRecordId = existingRecords[activityIndex]

                record = activityRow
                def jsonSlurper = new groovy.json.JsonSlurper()
                def activity = jsonSlurper.parseText(jsonStr)


                activity.projectId = Globals.PROJECT_ID
                  //Convert Date to UTC date.
                TimeZone tz = TimeZone.getTimeZone("UTC");
                java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                // Quoted "Z" to indicate UTC, no timezone offset
                df.setTimeZone(tz);
                java.text.DateFormat time = new java.text.SimpleDateFormat("hh:mm a");
                String isoDate = ''
                String isoDateTime = ''
                String isoFinishDate = ''
                String isoFinishTime = ''
                try {
                    isoDate = df.format(record.surveyStartDate);
                    //isoDateTime = '12:00 AM' //record."surveyStartTime" ? time.format(record."surveyStartTime") : ''
                    if (record.surveyStartTime)
                        isoDateTime = time.format(record.surveyStartTime)
                    if (record.surveyFinishDate)
                       isoFinishDate = df.format(record.surveyFinishDate)

                    if (record.surveyFinishTime)
                        if(record.surveyFinishTime instanceof String) {
                            java.text.DateFormat t = new java.text.SimpleDateFormat("hh:mm");
                            isoFinishTime= time.format(t.parse(record.surveyFinishTime))
                            println("${isoFinishTime} is obtained by parsing String")
                        }else
                            isoFinishTime =  time.format(record.surveyFinishTime)

                } catch (Exception ex) {
                    println( " ${activityIndex}th date/time format cannot be recognized")
                    println( record.surveyFinishTime.getClass() )
                    println(ex)
                    ex.printStackTrace()
                    System.exit(1)
                }



                // Upload photos to the stageing area.
                def sightingPhotos = []

                for (i = 0; i < 2; i++) {
                    def address = (i == 0) ? record.sightingPhoto : record.sightingPhoto1
                    if (address) {
                        def decoded = java.net.URLDecoder.decode(address, "UTF-8");
                        def fileNameToken = decoded?.split("&fileName=")
                        String fileName = fileNameToken?.size() > 0 ? fileNameToken[fileNameToken.size() - 1] : ""
                        fileName = activityIndex + "_" + i + "_" + fileName.replace(" ", "_")
                        List fileExtensionList = fileName?.tokenize(".")
                        String mimeType = fileExtensionList?.size() > 0 ? fileExtensionList[fileExtensionList.size() - 1] : ""
                        mimeType = mimeType?.toLowerCase()
                        switch (mimeType) {
                            case ".jpg":
                            case "jpg":
                            case ".jpeg":
                            case "jpeg":
                                mimeType = "image/jpeg"
                                break
                            case ".png":
                            case "png":
                                mimeType = "image/png"
                                break
                            default:
                                println("MIME type not supported." + mimeType)

                        }
                        println("Downloading image file...")
                        println("File name "+ IMAGES_PATH+fileName)
                        if (!DEBUG_AND_VALIDATE) {
                            try{
                                URL url = new URL(address)
                                File newFile = new File(IMAGES_PATH+fileName) << url.openStream()
                                def http = new HTTPBuilder(IMAGE_UPLOAD_URL)
                                println("Uploading image...")


                                http.request(Method.POST, ContentType.BINARY) { req ->
                                    requestContentType: "multipart/form-data"
                                    headers.userName = USERNAME
                                    headers.authKey = AUTH_KEY
                                    MultipartEntityBuilder multipartRequestEntity = new MultipartEntityBuilder()
                                    multipartRequestEntity.addPart('files', new FileBody(newFile, mimeType))
                                    req.entity = multipartRequestEntity.build()

                                    response.success = { resp, data ->
                                        // Convert to map
                                        def documents = data?.getText()
                                        if(documents) {
                                            def documentsMap = new JsonSlurper().parseText(documents)
                                            sightingPhotos << documentsMap?.files?.get(0)
                                            println("Image upload successful. - ${i}")
                                        } else {
                                            println("Image upload unsuccessful. - ${i}")
                                        }
                                    }
                                }
                            } catch(Exception e) {
                                println("Error downloading image" + e)
                            }

                        }
                    }
                }

                activity.outputs[0].data.surveyStartDate = isoDate
                activity.outputs[0].data.surveyStartTime = isoDateTime
                activity.outputs[0].data.surveyFinishDate = isoFinishDate
                activity.outputs[0].data.surveyFinishTime = isoFinishTime

                activity.outputs[0].data.surveyType = insertSpaces(record.surveyType)


                activity.outputs[0].data.notes = record."notes" ? (record."notes").trim() : ""
                activity.outputs[0].data.recordedBy = record."recordedBy" ? (record."recordedBy").trim() : ""

                activity.outputs[0].data.burnt = record."burnt"? Boolean.parseBoolean(record."burnt") : false
                activity.outputs[0].data.wetland = record."wetland" ? record."wetland" :  ""
                activity.outputs[0].data.cbocObserverCode = record."cbocObserverCode" ? record."cbocObserverCode" : ""
                activity.outputs[0].data.cbocSurveyReferenceNumber = record."cbocSurveyReferenceNumber"




                //Find siteId by locationId
                def locationId = record.locationId?record.locationId: record['verbatimLatitude']+"_"+record['verbatimLongitude']
                def currentSite  = sites.find{s -> s.locationId == locationId}
                activity.outputs[0].data.location = currentSite? currentSite.siteId : null
                activity.siteId = currentSite? currentSite.siteId : null

                activity.outputs[0].data.locationLatitude = Float.parseFloat(record["verbatimLatitude"])
                activity.outputs[0].data.locationLongitude = Float.parseFloat(record["verbatimLongitude"])

                // Custom mapping.

                activity.outputs[0].data.speciesSightings =[]
                def speciesSighting = [:]
                speciesSighting['sightingComments'] = record.sightingComments?record.sightingComments:''
                int individualCount = record.individualCount? (int) Float.parseFloat(record.individualCount):1
                if (individualCount == 0 )
                    speciesSighting['individualCount'] = 1
                else
                    speciesSighting['individualCount'] = individualCount


                speciesSighting['abundanceCode'] = record.abundanceCode
                speciesSighting['breedingStatus'] = insertSpaces(record.breedingStatus)
                speciesSighting['habitatCode'] = insertSpaces(record.habitatCode)
                speciesSighting['raouNumber'] = record.raouNumber



                if(record.species){
                    def species = [:]
                    speciesSighting['species'] = species
                    species['name'] = record.species
                    species['commonName'] = record.commonName

                    String encodedSurveyName = java.net.URLEncoder.encode(activity.outputs[0].name)
                    String encodedSpecies = java.net.URLEncoder.encode(record.'species')

                    // Get Unique Species Id
                    def uniqueIdResponse = new URL(Globals.SERVER_URL + "/ws/species/uniqueId")?.text
                    def jsonResponse = new groovy.json.JsonSlurper()
                    def outputSpeciesId = jsonResponse.parseText(uniqueIdResponse)?.outputSpeciesId
                    species['outputSpeciesId'] = outputSpeciesId


                    def speciesResponse = new URL(Globals.SPECIES_URL + "&q=${encodedSpecies}&output=${encodedSurveyName}&dataFieldName=species").text
                    def speciesJSON = new groovy.json.JsonSlurper()
                    def autoCompleteList = speciesJSON.parseText(speciesResponse)?.autoCompleteList
                      if (!autoCompleteList) {
                        species.name = record.'species'
                    }

                    autoCompleteList?.eachWithIndex { item, index ->
                        if (index == 0) {
                            species.name = item.name
                            species.guid = item.guid
                            species.scientificName = item.scientificName
                            species.commonName = item.commonName
                        }
                    }
                }




                speciesSighting['sightingPhoto'] = []

                for (i = 0; i < sightingPhotos.size(); i++) {
                    speciesSighting['sightingPhoto']  << sightingPhotos.get(i)
                }

                activity.outputs[0].data.speciesSightings.push(speciesSighting)


                if (Globals.DEBUG_AND_VALIDATE) {
                    println(new groovy.json.JsonBuilder( activity ).toPrettyString())
                    //System.exit(0)
                }

                String createdActivityId
                createdActivityId = postRecord(activity)
                //createdActivityId = updateRecord(existingRecordId,activity)

                if (createdActivityId){
                    if (Globals.DEBUG_AND_VALIDATE){
                        println(createdActivityId)
                       // System.exit(0)
                    }
                    recordsLog << Globals.SERVER_URL +'/bioActivity/index/' + createdActivityId +'\n'

                }else{
                    println('Error in creating records. System aborted')
                    System.exit(1)
                }

                print ("${activityIndex}th record edited ")
                createdActivityId ? println(" : ${createdActivityId}") : println()
            }
        }
    }


def postRecord(activity){
      def connection = new URL("${Globals.SERVER_URL}${Globals.ADD_NEW_ACTIVITY_URL}").openConnection() as HttpURLConnection
            // set some headers
        connection.setRequestProperty('userName', "${Globals.USERNAME}")
        connection.setRequestProperty('authKey', "${Globals.AUTH_KEY}")
        connection.setRequestProperty('Content-Type', 'application/json;charset=utf-8')
        connection.setRequestMethod("POST")
        connection.setDoOutput(true)

        java.io.OutputStreamWriter wr = new java.io.OutputStreamWriter(connection.getOutputStream(), 'utf-8')
        wr.write(new groovy.json.JsonBuilder(activity).toString())
        wr.flush()
        wr.close()

    if (connection.responseCode != 200) {
        def error = connection.getErrorStream().text
        def jsonSlurper = new JsonSlurper()
        def result = jsonSlurper.parseText(error)
        println(connection.responseCode + ": " + result.error)
        return null;
    }
    else{
        String resp =   connection.inputStream.text
        def jsonSlurper = new groovy.json.JsonSlurper()
        def result = jsonSlurper.parseText(resp)
        def activityId = result.resp.activityId
        return activityId
    }
}

//def updateRecord(activityId, activity){
//    def connection = new URL("${Globals.SERVER_URL}${Globals.EDIT_ACTIVITY_URL+'?id='+activityId}").openConnection() as HttpURLConnection
//    // set some headers
//    connection.setRequestProperty('userName', "${Globals.USERNAME}")
//    connection.setRequestProperty('authKey', "${Globals.AUTH_KEY}")
//    connection.setRequestProperty('Content-Type', 'application/json;charset=utf-8')
//    connection.setRequestMethod("POST")
//    connection.setDoOutput(true)
//
//    java.io.OutputStreamWriter wr = new java.io.OutputStreamWriter(connection.getOutputStream(), 'utf-8')
//    wr.write(new groovy.json.JsonBuilder(activity).toString())
//    wr.flush()
//    wr.close()
//
//    if (connection.responseCode != 200) {
//        def error = connection.getErrorStream().text
//        def jsonSlurper = new JsonSlurper()
//        def result = jsonSlurper.parseText(error)
//        println(connection.responseCode + ": " + result.error)
//        return null
//    }
//    else{
//        return activityId
//    }
//}

def deleteActivity(server_url, aid){
    def connection = new URL(server_url + '/delete/' + aid).openConnection() as HttpURLConnection
// set some headers
    connection.setRequestProperty('userName', Globals.USERNAME)
    connection.setRequestProperty('authKey', Globals.AUTH_KEY)
    connection.setRequestProperty('Cookie', 'recentHub=ala; recentHub=ala; _ga=GA1.3.166953798.1511148250; __utmz=8847461.1516579788.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __utmz=93619214.1518387953.35.8.utmcsr=auth.ala.org.au|utmccn=(referral)|utmcmd=referral|utmcct=/cas/login; _gid=GA1.3.471789785.1518387953; __utma=93619214.1320148222.1509067396.1518414557.1518474722.41; __utmb=93619214.0.10.1518474722; __utmc=93619214; __utma=8847461.166953798.1511148250.1516579788.1518474766.2; __utmc=8847461; ALA-Auth="qifeng.bai@csiro.au"; JSESSIONID=25599606E5756CDDDFC21B8F2C3BF2D2')
    connection.setRequestProperty('Content-Type', 'connection.setRequestProperty(\'authKey\', auth_key)')
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestMethod("POST")
    connection.setDoOutput(true)


    java.io.OutputStreamWriter wr = new java.io.OutputStreamWriter(connection.getOutputStream(), 'utf-8')
    wr.flush()
    wr.close()
    // get the response code - automatically sends the request
    def statusCode = connection.responseCode
    if (statusCode == 200 ){
        def result = connection.inputStream.text;
        println(result)
       }else {
        def error = connection.getErrorStream().text
        println(connection.responseCode + " : " + error)
     }

}





def loadXsl(String file){
    println("Reading file: " + file)

    def header = []
    def values = []

    Paths.get(file).withInputStream { input ->
//    def workbook = new XSSFWorkbook(input)
//    def sheet = workbook.getSheetAt(0)


        HSSFWorkbook workbook = new HSSFWorkbook(input)
        HSSFSheet sheet = workbook.getSheetAt(0)

        println("Loading sheet: " + sheet.sheetName)
        for (HSSFCell cell in sheet.getRow(0).cellIterator()) {
            header << cell.stringCellValue
        }

        def headerFlag = true

        for (HSSFRow row in sheet.rowIterator()) {
            if (headerFlag) {
                headerFlag = false
                continue
            }
            def rowData = [:]
            int i = 0
            for (HSSFCell cell in row.cellIterator()) {
                i++;
                def value = ''
                switch (cell.getCellType()) {
                    case HSSFCell.CELL_TYPE_STRING:
                        value = cell.stringCellValue
                        break
                    case HSSFCell.CELL_TYPE_NUMERIC:
                        if (org.apache.poi.hssf.usermodel.HSSFDateUtil.isCellDateFormatted(cell)) {
                            value = cell.getDateCellValue()
                        } else {
                            value = cell.numericCellValue as String
                        }

                        break
                    case HSSFCell.CELL_TYPE_BLANK:
                        value = ""
                    case HSSFCell.CELL_TYPE_BOOLEAN:
                        value = cell.booleanCellValue
                        break
                    case HSSFCell.CELL_TYPE_FORMULA:
                        switch (cell.getCachedFormulaResultType()) {
                            case HSSFCell.CELL_TYPE_NUMERIC:
                                value = cell.getNumericCellValue();
                                break;
                            case HSSFCell.CELL_TYPE_STRING:
                                value = cell.getRichStringCellValue()
                                break;
                        }
                    case HSSFCell.CELL_TYPE_ERROR:
                        value = "Error"
                        break
                    default:
                        println("Error: " + i + " Cell type not supported:" + cell.getCellType())
                        value = ''
                }

                rowData << [("${header[cell.columnIndex]}".toString()): value]
            }
            rowData << ["uniqueId": randomUUID() as String]
            values << rowData
        }
        println("Successfully loaded");
    }

    return values
}


def loadSites(String file){
    def sites = []
    File siteLog = new File( file)

    if (siteLog.exists()){
        siteLog.eachLine { String line ->
            def fields = line.split('\t')
            def site = [:]
            site['siteId'] = fields[0]
            site.siteName = fields[2]
            site['locationId'] = fields[1]

            sites.push(site)
        }
        println ("Load "+ sites.size() + " existing sites")
    }

    return sites;

}

def createSites(activities, existingSites,siteLogFile){
    //Create sites without duplciation
    activities?.eachWithIndex { activityRow, activityIndex ->
        if (activityIndex < 20){
            def name = activityRow['siteName'] ? activityRow['siteName'] :'batch created'
            def latitude = activityRow.verbatimLatitude ? activityRow.verbatimLatitude: 0
            def longitude = activityRow.verbatimLongitude ? activityRow.verbatimLongitude: 0
            def locationId = activityRow.locationId ? activityRow.locationId : latitude+"_"+longitude

            def found = existingSites.find{s -> s.locationId == locationId}
            if (!found){
                def extent=[
                        source:"Point",
                        "geometry": [
                                "centre": [
                                        longitude,
                                        latitude
                                ],
                                "type": "Point",
                                "areaKmSq": 0,
                                "coordinates": [
                                        longitude,
                                        latitude
                                ]
                        ]
                ]

                def site = [
                        name: name,
                        visibility: 'public', //otherwise, it cannot pass permission check
                        projects:[Globals.PROJECT_ID],
                        extent: extent
                ]

                def payload = new groovy.json.JsonBuilder([
                        pActivityId: Globals.PROJECT_ACTIVITY_ID,
                        site:site
                ])

                def siteId = uploadSite(Globals.SERVER_URL+Globals.SITE_CREATION_URL, payload)

                if (siteId) {
                    site.siteId = siteId
                    println siteId +" is created"
                }
                else{
                    println 'site is not created properly'
                    System.exit(0)
                }
                //add locationid to site
                site.locationId = locationId
                existingSites.push(site)
            }
        }
    }

    println (existingSites.size() + ' sites in Total')

    File siteLog = new File( siteLogFile)
    if (siteLog.exists())
        siteLog.delete()
    existingSites.each {st ->
        def line = st.siteId +"\t" + st.locationId +"\t"+st.name+'\n'
        siteLog << line
    }
    return existingSites
}


def uploadSite(server_url, site){
    def connection = new URL(server_url).openConnection() as HttpURLConnection
// set some headers
    connection.setRequestProperty('userName', Globals.USERNAME)
    connection.setRequestProperty('authKey', Globals.AUTH_KEY)
// connection.setRequestProperty('Cookie', 'recentHub=ala; _ga=GA1.3.166953798.1511148250; _gid=GA1.3.442296331.1516576819; __utma=8847461.166953798.1511148250.1516579788.1516579788.1; __utmc=8847461; __utmz=8847461.1516579788.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __utmc=90279561; recentHub=ala; ALA-Auth="qifeng.bai@csiro.au"; JSESSIONID=35157A1019A90D4336D79217F55223A5; _gat=1; __utma=90279561.1366919216.1503969276.1516665164.1516685701.62; __utmb=90279561.0.10.1516685701; __utmz=90279561.1516685701.62.11.utmcsr=auth.ala.org.au|utmccn=(referral)|utmcmd=referral|utmcct=/cas/login')
// connection.setRequestProperty('Content-Type', 'connection.setRequestProperty(\'authKey\', auth_key)')
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestMethod("POST")
    connection.setDoOutput(true)


        java.io.OutputStreamWriter wr = new java.io.OutputStreamWriter(connection.getOutputStream(), 'utf-8')
        wr.write(site.toString())
        wr.flush()
        wr.close()
        // get the response code - automatically sends the request
        def statusCode = connection.responseCode
        if (statusCode == 200 ){
            def result = connection.inputStream.text;
            def jsonSlurper = new JsonSlurper()
            def site_obj = jsonSlurper.parseText(result)
            return site_obj.id
        }else{
            def error = connection.getErrorStream().text
            println(connection.responseCode + " : " + error)
            def jsonSlurper = new JsonSlurper()
            def result = jsonSlurper.parseText(error)
            //401 authentication error may still create site , why? don't know
            if (result.status == "created")
                return result.id
            else
                return null;
        }

}

/**
 * Insert spaces before/after - .
 *
 * @param value
 */
def insertSpaces(String value){
    //[^\s]-[^\s]   -> find - without spaces before/after
    if (value){
        String match = value.find(/[^\s]-[^\s]/)
        if (match){
            match = match.replace('-', ' - ')
            value = value.replaceFirst(/[^\s]-[^\s]/, match)
        }
    }
    return value
}

