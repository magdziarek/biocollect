<div>
    <!-- ko with: mapConfiguration -->
    <div data-bind="css: {'ajax-opacity': transients.loading}">
        <div class="row-fluid">
            <div class="span12 text-left">
                <h2 class="strong"><g:message code="mapConfiguration.heading"/></h2>
            </div>
        </div>

        <div class="row-fluid">
            <div class="span12">
                <h3><g:message code="mapConfiguration.step.one.title"/></h3>
            </div>
        </div>

        <div class="accordion" id="site-accordion">
            <div class="accordion-group">
                <div class="accordion-heading">
                    <a class="accordion-toggle" data-toggle="collapse" data-parent="#site-accordion" href="#site-pick"
                       data-bind="click: transients.setSurveySiteOption.bind({value: 'sitepick'})">
                        <div class="large-checkbox">
                            <input type="checkbox" name="scienceType"
                                   data-bind="checked: surveySiteOption() === 'sitepick'" value="sitepick"/>
                            <label><span></span> <g:message code="mapConfiguration.sites.pick.title"/></label>
                        </div>
                    </a>
                </div>

                <div id="site-pick" class="accordion-body collapse"  data-bind="css: { 'in': transients.surveySiteOption == 'sitepick' }">
                    <div class="accordion-inner">
                        <div data-bind="if: surveySiteOption() === 'sitepick', slideVisible: surveySiteOption() === 'sitepick'">
                            <label class="padding-top-10"><strong><g:message
                                    code="mapConfiguration.user.pick.site.title"/></strong></label>
                            <!-- ko template: {name: 'template-sites-pick-one'} -->
                            <!-- /ko -->

                            <label class="padding-top-20"><strong><g:message
                                    code="mapConfiguration.map.behaviour.title"/></strong></label>
                            <!-- ko template: {name: 'template-site-zoom'} -->
                            <!-- /ko -->

                        </div>
                    </div>
                </div>
            </div>

            <div class="accordion-group">
                <div class="accordion-heading">
                    <a class="accordion-toggle" data-toggle="collapse" data-parent="#site-accordion" href="#site-create"
                       data-bind="click: transients.setSurveySiteOption.bind({value:'sitecreate'})">
                        <div class="large-checkbox">
                            <input type="checkbox" name="scienceType"
                                   data-bind="checked: surveySiteOption() === 'sitecreate'" value="sitecreate"/>
                            <label><span></span> <g:message code="mapConfiguration.sites.create.title"/></label>
                        </div>
                    </a>
                </div>

                <div id="site-create" class="accordion-body collapse"  data-bind="css: { 'in': transients.surveySiteOption == 'sitecreate' }">
                    <div class="accordion-inner">
                        <div data-bind="if: surveySiteOption() === 'sitecreate', slideVisible: surveySiteOption() === 'sitecreate'">
                            <label class="padding-top-10"><strong><g:message
                                    code="mapConfiguration.user.created.site.title"/></strong></label>
                            <!-- ko template: {name: 'template-site-create'} -->
                            <!-- /ko -->

                            <label class="padding-top-20"><strong><g:message
                                    code="mapConfiguration.map.behaviour.title"/></strong></label>
                            <!-- ko template: {name: 'template-site-zoom'} -->
                            <!-- /ko -->
                        </div>
                    </div>
                </div>
            </div>

            <div class="accordion-group">
                <div class="accordion-heading">
                    <a class="accordion-toggle" data-toggle="collapse" data-parent="#site-accordion"
                       href="#site-pick-create" data-bind="click: transients.setSurveySiteOption.bind({value:'sitepickcreate'})">
                        <div class="large-checkbox">
                            <input type="checkbox" name="scienceType"
                                   data-bind="checked: surveySiteOption() === 'sitepickcreate'" value="sitepickcreate"/>
                            <label><span></span> <g:message code="mapConfiguration.sites.both.title"/></label>
                        </div>
                    </a>
                </div>

                <div id="site-pick-create" class="accordion-body collapse" data-bind="css: { 'in': transients.surveySiteOption == 'sitepickcreate' }">
                    <div class="accordion-inner">
                        <div data-bind="if: surveySiteOption() === 'sitepickcreate', slideVisible: surveySiteOption() === 'sitepickcreate'">
                            <label class="padding-top-10"><strong><g:message
                                    code="mapConfiguration.user.pick.site.title"/></strong></label>
                            <!-- ko template: {name: 'template-sites-pick-one'} -->
                            <!-- /ko -->

                            <label class="padding-top-20"><strong><g:message
                                    code="mapConfiguration.user.created.site.title"/></strong></label>
                            <!-- ko template: {name: 'template-site-create'} -->
                            <!-- /ko -->

                            <label class="padding-top-20"><strong><g:message
                                    code="mapConfiguration.map.behaviour.title"/></strong></label>
                            <!-- ko template: {name: 'template-site-add-to-project'} -->
                            <!-- /ko -->
                            <!-- ko template: {name: 'template-site-zoom'} -->
                            <!-- /ko -->
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="row-fluid">
            <div class="span12">
                <h3><g:message code="mapConfiguration.step.two.title"/></h3>
            </div>
        </div>

        <div class="row-fluid">
            <div class="span12">
                <map-config-selector
                        params="allBaseLayers: fcConfig.allBaseLayers, allOverlays: fcConfig.allOverlays, mapLayersConfig: $parent.mapLayersConfig"></map-config-selector>
            </div>
        </div>


        <div class="row-fluid">
            <div class="span12">
                <button class="btn-primary btn btn-small block" data-bind="click: $parent.saveMapConfig"><i
                        class="icon-white  icon-hdd"></i>  Save</button>
            </div>
        </div>
    </div>
    <!-- /ko -->
</div>
<script>

</script>

<script id="template-sites-pick-one" type="text/html">
<div data-validation-engine="validate[funcCall[isSiteSelectionConfigValid]]" data-prompt-position="inline"
     data-position-type="inline" data-prompt-target="sites-pick-one-message-container">
    <div id="sites-pick-one-message-container"></div>

    <div id="survey-site-list" class="row-fluid">
        <div class="span12">
            <div style="max-height: 500px; overflow-y: auto;">
                <!-- ko foreach: transients.sites -->
                <div class="row-fluid">
                    <div class="span6">
                        <label class="checkbox">
                            <input type="checkbox" data-bind="checkedValue: $data.siteId, checked: $parent.sites">
                            <a class="btn-link" target="_blank"
                               data-bind="attr:{href: $parent.transients.siteUrl($data)}, text: name"></a>
                        </label>
                    </div>

                    <div class="span6">
                        <a class="btn btn-mini btn-default" target="_blank"
                           data-bind="attr:{href: $parent.transients.siteUrl($data)}" role="button">
                            <i class="icon-eye-open"></i>
                            <g:message code="btn.view"/>
                        </a>
                        <button class="btn btn-mini btn-danger"
                                data-bind="click: $parent.transients.deleteSite, disable: $parent.transients.isSiteDeleteDisabled.bind($data)()">
                            <i class="icon-remove icon-white"></i>
                            <g:message code="btn.delete"/>
                        </button>
                    </div>
                </div>
                <!-- /ko -->
            </div>
            <!-- ko if: transients.sites.length == 0 -->
            <div class="alert-info">
                <g:message code="mapConfiguration.site.selection.title"/>
            </div>
            <!-- /ko -->
        </div>
    </div>

    <div class="row-fluid">
        <div class="span12">
            <label>
                <g:message code="mapConfiguration.site.create.choose.title"></g:message>
                <button class="btn-default btn btn-small block" data-bind="click: $parent.redirectToSelect"><i
                        class="icon-folder-open"></i> <g:message code="mapConfiguration.site.existing.selection"/>
                </button>
                <button class="btn-default btn btn-small block" data-bind="click: $parent.redirectToCreate"><i
                        class="icon-plus"></i> <g:message code="mapConfiguration.site.create"/>
                </button>
                <button class="btn-default btn btn-small block" data-bind="click: $parent.redirectToUpload"><i
                        class="icon-arrow-up"></i> <g:message code="mapConfiguration.site.upload"/></button>
            </label>
        </div>
    </div>
</div>
</script>
<script id="template-site-create" type="text/html">
<div>
    <div id="site-create-message-container"></div>
    <div class="row-fluid" data-validation-engine="validate[funcCall[isUserSiteCreationConfigValid]]"
         data-prompt-position="inline" data-position-type="inline" data-prompt-target="site-create-message-container">
        <div class="span12">
            <div id="survey-site-create" class="row-fluid">
                <div class="span12">
                    <label class="checkbox">
                        <input type="checkbox" data-bind="checked: allowPoints"/>  <g:message code="mapConfiguration.site.point"/>
                    </label>
                    <label class="checkbox">
                        <input type="checkbox" data-bind="checked: allowPolygons"/> <g:message code="mapConfiguration.site.polygon"/>
                    </label>
                    <label class="checkbox">
                        <input type="checkbox" data-bind="checked: allowLine"/> <g:message code="mapConfiguration.site.line"/>
                    </label>
                </div>
            </div>
        </div>
    </div>
</div>
</script>
<script id="template-site-add-to-project" type="text/html">
<div class="row-fluid">
    <div class="span6">
        <label class="checkbox">
            <input type="checkbox"
                   data-bind="checked: addCreatedSiteToListOfSelectedSites, disable: !!isUserSiteCreationConfigValid()"/> <g:message
                code="mapConfiguration.site.create.add.to.project"/>
        </label>
        <span class="help-block"><g:message
                code="mapConfiguration.addCreatedSiteToListOfSelectedSites.help.text"/></span>
    </div>
</div>
</script>
<script id="template-site-zoom" type="text/html">
<div class="row-fluid">
    <div class="span6">
        <label>
            <g:message code="mapConfiguration.zoom.area"/>
            <select id="siteToZoom"
                    data-bind='options: transients.sites, optionsText: "name", optionsValue: "siteId", value: defaultZoomArea;'></select>
        </label>
        <span class="help-block"><g:message code="mapConfiguration.zoom.area.help.text"/> </span>
    </div>
</div>
</script>