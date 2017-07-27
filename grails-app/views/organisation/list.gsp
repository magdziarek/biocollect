<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${hubConfig.skin}"/>
    <title>Organisations | Field Capture</title>
    <meta name="breadcrumbParent1" content="${createLink(controller: 'project', action: 'projectFinder')},Home"/>
    <meta name="breadcrumb" content="Organisations"/>

    <script type="text/javascript" src="${grailsApplication.config.google.maps.url}" async defer></script>
    <r:script disposition="head">
        var fcConfig = {
            serverUrl: "${grailsApplication.config.grails.serverURL}",
            createOrganisationUrl: "${createLink(controller: 'organisation', action: 'create')}",
            viewOrganisationUrl: "${createLink(controller: 'organisation', action: 'index')}",
            organisationSearchUrl: "${createLink(controller: 'organisation', action: 'search')}",
            noLogoImageUrl: "${r.resource(dir:'images', file:'no-image-2.png')}"
            };
    </r:script>
    <r:require modules="knockout,amplify,organisation"/>

</head>

<body>
    <g:render template="organisationListing"></g:render>

</body>

</html>