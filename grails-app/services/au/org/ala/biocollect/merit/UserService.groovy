package au.org.ala.biocollect.merit

import au.org.ala.biocollect.merit.hub.HubSettings

import javax.annotation.PostConstruct
import org.apache.commons.httpclient.HttpStatus

class UserService {
   def grailsApplication, authService, webService
    def auditBaseUrl = ""

    static String USER_NAME_HEADER_FIELD = "userName"
    static String AUTH_KEY_HEADER_FIELD = "authKey"

    @PostConstruct
    private void init() {
        try {
            auditBaseUrl = grailsApplication.config.ecodata.service.url + '/audit'
        } catch (e) {
            log.debug("This configuration property does not get initialized in test environment", e)
        }
    }

    def getCurrentUserDisplayName() {
        getUser()?.displayName?:""
    }

    def getCurrentUserId(request = null) {
        def userId = ""

        if (request) {
            String username = request.getHeader(UserService.USER_NAME_HEADER_FIELD)
            String key = request.getHeader(UserService.AUTH_KEY_HEADER_FIELD)
            userId = username && key ? getUserFromAuthKey(username, key)?.userId : ''
        }

        userId ?: (getUser()?.userId ?: "")
    }

   /*
    * Get User details for the given user name and auth key.
    *
    * @param username username
    * @param key mobile auth key
    * @return userdetails
    * */

    def UserDetails getUserFromAuthKey(String username, String key) {
        String url = grailsApplication.config.mobile.auth.check.url
        Map params = [userName: username, authKey: key]
        def result = webService.doPostWithParams(url, params)

        if (result.statusCode == HttpStatus.SC_OK && result.resp?.status == 'success') {
            params = [userName: username]
            url = grailsApplication.config.userDetails.url + "getUserDetails"
            result = webService.doPostWithParams(url, params)
            if (result.statusCode == HttpStatus.SC_OK && result.resp) {
                return new UserDetails(result.resp.firstName + result.resp.lastName, result.resp.userName, result.resp.userId)
            }
        }

        return null
    }


    public UserDetails getUser() {
        def u = authService.userDetails()
        def user

        if (u?.userId) {
            user = new UserDetails(u.userDisplayName, u.email, u.userId)
        }

        return user
    }

    /**
     * Hubs currently only support public, logged in user and admin roles so this implementation doesn't need
     * to call ecodata to use the AccessLevel comparison.  This avoids the need to cache the result, at least for the
     * moment, as this method could be called many times while rendering a hub skin.
     * @param role the role to check.
     * @return true if the user has the supplied role.
     */
    boolean doesUserHaveHubRole(String role) {
        HubSettings settings = SettingService.getHubConfig()
        String userId = getUser()?.userId
        if (!userId) {
            return false
        }
        if (userIsAlaAdmin()) {
            return true
        }
        if (role == RoleService.LOGGED_IN_USER_ROLE) {
            return true
        }

        return role == settings.userPermissions?.find({it.userId == userId})?.accessLevel
    }

    def userInRole(role) {
        authService.userInRole(role)
    }

    def userIsSiteAdmin() {
        authService.userInRole(grailsApplication.config.security.cas.officerRole) || authService.userInRole(grailsApplication.config.security.cas.adminRole) || authService.userInRole(grailsApplication.config.security.cas.alaAdminRole)
    }

    Boolean  userIsAlaAdmin() {
        authService.userInRole(grailsApplication.config.security.cas.alaAdminRole)
    }

    def userIsAlaOrFcAdmin() {
        authService.userInRole(grailsApplication.config.security.cas.adminRole) || authService.userInRole(grailsApplication.config.security.cas.alaAdminRole)
    }

    def userHasReadOnlyAccess() {
        authService.userInRole(grailsApplication.config.security.cas.readOnlyOfficerRole)
    }

    def getRecentEditsForUserId(userId) {
        def url = auditBaseUrl + "/getRecentEditsForUserId/${userId}"
        webService.getJson(url)
    }

    def getProjectsForUserId(userId) {
        def url = grailsApplication.config.ecodata.service.url + "/permissions/getProjectsForUserId/${userId}"
        webService.getJson(url)
    }

    def getOrganisationIdsForUserId(userId) {
        def url = grailsApplication.config.ecodata.service.url + "/permissions/getOrganisationIdsForUserId/${userId}"
        webService.getJson(url)
    }

    def getOrganisationsForUserId(userId) {
        def url = grailsApplication.config.ecodata.service.url + "/permissions/getOrganisationsForUserId/${userId}"
        webService.getJson(url)
    }

    def getStarredProjectsForUserId(userId) {
        def url = grailsApplication.config.ecodata.service.url + "/permissions/getStarredProjectsForUserId/${userId}"
        webService.getJson(url)
    }

    def getStarredSiteIdsForUserId(String userId) {
        def url = grailsApplication.config.ecodata.service.url + "/permissions/getStarredSiteIdsForUserId/${userId}"
        webService.getJson(url)
    }



    def isProjectStarredByUser(String userId, String projectId) {
        def url = grailsApplication.config.ecodata.service.url + "/permissions/isProjectStarredByUser?userId=${userId}&projectId=${projectId}"
        webService.getJson(url)
    }

    def addStarProjectForUser(String userId, String projectId) {
        def url = grailsApplication.config.ecodata.service.url + "/permissions/addStarProjectForUser?userId=${userId}&projectId=${projectId}"
        webService.getJson(url)
    }

    def removeStarProjectForUser(String userId, String projectId) {
        def url = grailsApplication.config.ecodata.service.url + "/permissions/removeStarProjectForUser?userId=${userId}&projectId=${projectId}"
        webService.getJson(url)
    }

    Map isUserInRoleForProject(String userId, String projectId, String role) {
        def url = grailsApplication.config.ecodata.service.url + "/permissions/isUserInRoleForProject?userId=${userId}&projectId=${projectId}&role=${role}"
        webService.getJson(url)
    }

    def addStarSiteForUser(String userId, String siteId) {
        def url = grailsApplication.config.ecodata.service.url + "/permissions/addStarSiteForUser"
        webService.doPostWithParams(url, [userId: userId, siteId: siteId])
    }

    def removeStarSiteForUser(String userId, String siteId) {
        def url = grailsApplication.config.ecodata.service.url + "/permissions/removeStarSiteForUser"
        webService.doPostWithParams(url, [userId: userId, siteId: siteId])
    }

    def isSiteStarredByUser(String userId, String siteId) {
        def url = grailsApplication.config.ecodata.service.url + "/permissions/isSiteStarredByUser?userId=${userId}&siteId=${siteId}"
        webService.getJson(url)
    }

    def addUserAsRoleToProject(String userId, String projectId, String role) {
        def url = grailsApplication.config.ecodata.service.url + "/permissions/addUserAsRoleToProject?userId=${userId}&projectId=${projectId}&role=${role}"
        webService.getJson(url)
    }

    def removeUserWithRole(projectId, userId, role) {
        def url = grailsApplication.config.ecodata.service.url + "/permissions/removeUserWithRoleFromProject?projectId=${projectId}&userId=${userId}&role=${role}"
        webService.getJson(url)
    }

    def addUserAsRoleToOrganisation(String userId, String organisationId, String role) {
        def url = grailsApplication.config.ecodata.service.url + "/permissions/addUserAsRoleToOrganisation?userId=${userId}&organisationId=${organisationId}&role=${role}"
        webService.getJson(url)
    }

    def removeUserWithRoleFromOrganisation(String userId, String organisationId, String role) {
        def url = grailsApplication.config.ecodata.service.url + "/permissions/removeUserWithRoleFromOrganisation?organisationId=${organisationId}&userId=${userId}&role=${role}"
        webService.getJson(url)
    }

    def isUserAdminForProject(userId, projectId) {
        def url = grailsApplication.config.ecodata.service.url + "/permissions/isUserAdminForProject?projectId=${projectId}&userId=${userId}"
        def results = webService.getJson(url)
        return results?.userIsAdmin
    }

    def isUserCaseManagerForProject(userId, projectId) {
        def url = grailsApplication.config.ecodata.service.url + "/permissions/isUserCaseManagerForProject?projectId=${projectId}&userId=${userId}"
        def results = webService.getJson(url)
        return results?.userIsCaseManager
    }

    def isUserEditorForProject(userId, projectId) {
        def url = grailsApplication.config.ecodata.service.url + "/permissions/isUserEditorForProject?projectId=${projectId}&userId=${userId}"
        def results = webService.getJson(url)
        return results?.userIsEditor
    }

    def canUserEditProject(userId, projectId) {
        def url = grailsApplication.config.ecodata.service.url + "/permissions/canUserEditProject?projectId=${projectId}&userId=${userId}"
        def results = webService.getJson(url)
        return results?.userIsEditor
    }

    def isUserAdminForOrganisation(userId, organisationId) {
        def url = grailsApplication.config.ecodata.service.url + "/permissions/isUserAdminForOrganisation?organisationId=${organisationId}&userId=${userId}"
        def results = webService.getJson(url)
        return results?.userIsAdmin
    }

    def isUserGrantManagerForOrganisation(userId, organisationId) {
        def url = grailsApplication.config.ecodata.service.url + "/permissions/isUserGrantManagerForOrganisation?organisationId=${organisationId}&userId=${userId}"
        def results = webService.getJson(url)
        return results?.userIsGrantManager
    }

    def checkEmailExists(String email) {
        def user = authService.getUserForEmailAddress(email)
        return user?.userId
    }
}
