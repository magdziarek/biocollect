<div id="edit-meri-plan">
	<span data-bind="if:detailsLastUpdated"> <br/>Last update date : <span data-bind="text:detailsLastUpdated.formattedDate"></span></span>

	<div class="row-fluid space-after">
		<div>
			<div class="well well-small">
				<label><b>Project Outcomes</b></label>
				<table style="width: 100%;">
					<thead>
					<tr>
						<th></th>
						<th>Outcomes <fc:iconHelp title="Outcomes">Enter the outcomes sought by the project. This should be expressed as a 'SMART' statement (Specific Measurable Attainable Realistic and Time-bound) and deliver against the programme.  The outcome should be no more than 2 sentences.</fc:iconHelp></th>
						<th>
							Asset(s) addressed <fc:iconHelp title="Asset(s) addressed">Select the most appropriate natural/cultural asset or assets being addressed by this project from the drop down list. Note that multiple selections can be made. </fc:iconHelp>
						</br> (Hold down the Ctrl key and click to select multiple values.)
						</th>
					</tr>
					</thead>
					<tbody data-bind="foreach : details.objectives.rows1">
					<tr>
						<td width="2%"> <span data-bind="text:$index()+1"></span></td>
						<td width="54%"><textarea style="width: 99%;" data-bind="value: description, disable: $parent.isProjectDetailsLocked()" rows="5" ></textarea></td>
						<td width="40%"><select style="width: 99%;float:right;" class="input-xlarge"
												data-bind="options: $parent.protectedNaturalAssests, selectedOptions: assets, disable: $parent.isProjectDetailsLocked()" size="5" multiple="true"></select></td>
						<td width="4%">
							<span data-bind="if: $index() && !$parent.isProjectDetailsLocked()"><i class="icon-remove" data-bind="click: $parent.removeObjectivesOutcome"></i></span>
						</td>
					</tr>
					</tbody>
					<tfoot>
					<tr>
						<td></td>
						<td colspan="0" style="text-align:left;">
							<button type="button" class="btn btn-small" data-bind="disable:isProjectDetailsLocked(), click: addOutcome">
								<i class="icon-plus"></i> Add a row</button>
						</td>
						<td></td>
						<td></td>
					</tr>
					</tfoot>
				</table>
				<br/>
				<table style="width: 100%;">
					<thead>
					<tr>
						<th></th>
						<th>Monitoring indicator<fc:iconHelp title="Monitoring indicator">List the indicators of project success that will be monitored. Add a new row for each indicator, e.g. ground cover condition, increased abundance of a particular species, increased engagement of community in delivery of on-ground works.</fc:iconHelp></th>
						<th>Monitoring approach <fc:iconHelp title="Monitoring approach">How will this indicator be monitored? Briefly describe the method to be used to monitor the indicator.</fc:iconHelp></th>
						<th></th>
					</tr>
					</thead>
					<tbody data-bind="foreach : details.objectives.rows">
					<tr>
						<td width="2%"> <span data-bind="text:$index()+1"></span></td>
						<td width="30%"> <textarea style="width: 97%;" data-bind="value: data1, disable: $parent.isProjectDetailsLocked()" rows="3"> </textarea></td>
						<td width="64%"> <textarea style="width: 97%;" data-bind="value: data2, disable: $parent.isProjectDetailsLocked()" rows="5" ></textarea> </td>
						<td width="4%">
							<span data-bind="if: $index() && !$parent.isProjectDetailsLocked()"><i class="icon-remove" data-bind="click: $parent.removeObjectives"></i></span>
						</td>
					</tr>
					</tbody>
					<tfoot>
					<tr>
						<td></td>
						<td colspan="0" style="text-align:left;">
							<button type="button" class="btn btn-small" data-bind="disable:isProjectDetailsLocked(), click: addObjectives">
								<i class="icon-plus"></i> Add a row</button>
						</td>
					</tr>
					</tfoot>
				</table>
			</div>
		</div>
	</div>


	<div class="row-fluid space-after">
		<div>
			<div id="national-priorities" class="well well-small">
				<label><b>National and regional priorities</b></label>
				<p>Explain how the project aligns with all applicable national and regional priorities, plans and strategies.</p>
				<table style="width: 100%;">
					<thead>
					<tr>
						<th></th>
						<th>Document name <fc:iconHelp title="Document name">List the name of the National or Regional plan the project is addressing.</fc:iconHelp></th>
						<th>Relevant section <fc:iconHelp title="Relevant section">What section (target/outcomes/objective etc) of the plan is being addressed?</fc:iconHelp></th>
						<th>Explanation of strategic alignment <fc:iconHelp title="Explanation of strategic alignment">In what way will the project deliver against this section? Keep the response brief, 1 to 2 sentences should be adequate.</fc:iconHelp></th>
						<th></th>
					</tr>
					</thead>
					<tbody data-bind="foreach : details.priorities.rows">
					<tr>
						<td width="2%"> <span data-bind="text:$index()+1"></span></td>
						<td width="30%"> <textarea style="width: 97%;" class="input-xlarge"  data-bind="value: data1, disable: $parent.isProjectDetailsLocked()" rows="3"> </textarea></td>
						<td width="32%"> <textarea style="width: 97%;" class="input-xlarge" data-bind="value: data2, disable: $parent.isProjectDetailsLocked()"  rows="5"></textarea></td>
						<td width="32%"> <textarea style="width: 97%;" class="input-xlarge" data-bind="value: data3, disable: $parent.isProjectDetailsLocked()"  rows="5"></textarea></td>
						<td width="4%">
							<span data-bind="if: $index() && !$parent.isProjectDetailsLocked()"><i class="icon-remove" data-bind="click: $parent.removeNationalAndRegionalPriorities"></i></span>
						</td>
					</tr>
					</tbody>
					<tfoot>
					<tr>
						<td></td>
						<td colspan="0" style="text-align:left;">
							<button type="button" class="btn btn-small" data-bind="disable: isProjectDetailsLocked(), click: addNationalAndRegionalPriorities">
								<i class="icon-plus"></i> Add a row</button></td>
					</tr>
					</tfoot>
				</table>
			</div>
		</div>
	</div>


	<div class="row-fluid space-after">
		<div>
			<div id="project-implementation" class="well well-small">
				<label><b>Project implementation / delivery mechanism</b></label>
				<p>Explain how the project will be implemented, including methods, approaches, collaborations, etc. (5000 character limit) <b><fc:iconHelp title="Project implementation / delivery mechanism">How is the project to be delivered? Briefly describe the high level method/s to be used. The delivery mechanism/s should provide sufficient detail to understand how the project's outcomes will be achieved.</fc:iconHelp></b></p>
				<textarea style="width: 98%;" maxlength="5000"
						  data-bind="value:details.implementation.description, disable: isProjectDetailsLocked()"
						  class="input-xlarge" id="implementation" rows="10" ></textarea>
			</div>
		</div>
	</div>

	<div class="row-fluid space-after">
		<div id="project-partnership" class="well well-small">
			<label><b>Project partnerships</b></label>
			<p>Provide details on all project partners and the nature and scope of their participation in the project.</p>
			<table style="width: 100%;">
				<thead>
				<tr>
					<th></th>
					<th>Partner name
						<fc:iconHelp title="Partner name">Name of project partner, to be a project partner they need to be actively involved in the planning or delivery of the project.</fc:iconHelp></th>
					<th>Nature of partnership<fc:iconHelp title="Nature of partnership">Very briefly indicate how the partner is contributing to the project.</fc:iconHelp></th>
					<th>Type of organisation<fc:iconHelp title="Type of organisation">Select the most appropriate partner type from the list provided.</fc:iconHelp></th>
					<th></th>
				</tr>
				</thead>
				<tbody data-bind="foreach : details.partnership.rows">
				<tr>
					<td width="2%"> <span data-bind="text:$index()+1"></span></td>
					<td width="20%"> <textarea style="width: 97%;" class="input-xlarge"  data-bind="value: data1, disable: $parent.isProjectDetailsLocked()" rows="3"></textarea> </td>
					<td width="54%"><textarea style="width: 97%;" class="input-xlarge" data-bind="value: data2, disable: $parent.isProjectDetailsLocked()"  rows="5"></textarea></td>
					<td width="20%"><select style="width: 97%;" class="input-xlarge" data-bind="options: $parent.organisations, value:data3,optionsCaption: 'Please select',disable: $parent.isProjectDetailsLocked()"></select></td>
					<td width="4%">
						<span data-bind="if: $index() && !$parent.isProjectDetailsLocked()" ><i class="icon-remove" data-bind="click: $parent.removePartnership"></i></span>
					</td>
				</tr>
				</tbody>
				<tfoot>
				<tr>
					<td></td>
					<td colspan="0" style="text-align:left;">
						<button type="button" class="btn btn-small"  data-bind="disable: isProjectDetailsLocked(), click: addPartnership">
							<i class="icon-plus"></i> Add a row</button></td>
				</tr>
				</tfoot>
			</table>
		</div>
	</div>

	<div class="row-fluid space-after">
		<div>
			<div id="keq" class="well well-small">
				<label><b>Key evaluation question</b>  <fc:iconHelp title="Key evaluation question">Please list the Key Evaluation Questions for your project. Evaluation questions should cover the effectiveness of the project and whether it delivered what was intended; the impact of the project; the efficiency of the delivery mechanism/s; and the appropriateness of the methodology. These need to be answerable within the resources and time available to the project.</fc:iconHelp></label>
				<table style="width: 100%;">
					<thead>
					<tr>
						<th></th>
						<th>Project Key evaluation question (KEQ)
							<fc:iconHelp title="Project Key evaluation question (KEQ)">List the projects KEQ’s. Add rows as necessary.</fc:iconHelp></th>
						<th>How will KEQ be monitored
							<fc:iconHelp title="How will KEQ be monitored">Briefly describe how the project will ensure that evaluation questions will be addressed in a timely and appropriate manner.</fc:iconHelp></th>
						<th></th>
					</tr>
					</thead>
					<tbody data-bind="foreach : details.keq.rows">
					<tr>
						<td width="2%"> <span data-bind="text:$index()+1"></span></td>
						<td width="32%">
							<textarea style="width: 97%;" rows="3"  class="input-xlarge"  data-bind="value: data1, disable: $parent.isProjectDetailsLocked()">
							</textarea>
						</td>
						<td width="52%"><textarea style="width: 97%;" class="input-xlarge" data-bind="value: data2, disable: $parent.isProjectDetailsLocked()"  rows="5"></textarea></td>
						<td width="4%">
							<span data-bind="if: $index() && !$parent.isProjectDetailsLocked()" ><i class="icon-remove" data-bind="click: $parent.removeKEQ"></i></span>
						</td>
					</tr>
					</tbody>
					<tfoot>
					<tr>
						<td></td>
						<td colspan="0" style="text-align:left;">
							<button type="button" class="btn btn-small" data-bind="disable: isProjectDetailsLocked(), click: addKEQ">
								<i class="icon-plus"></i> Add a row</button></td>
					</tr>
					</tfoot>
				</table>
			</div>
		</div>
	</div>


	<div id="save-details-result-placeholder"></div>

	<div class="row-fluid space-after">
		<div class="span12">
			<div class="form-actions">

				<button type="button" data-bind="click: saveMeriPlan" id="project-details-save" class="btn btn-primary">Save changes</button>
				<button type="button" id="details-cancel" class="btn" data-bind="click: cancelMeriPlanEdits">Cancel</button>
			</div>

		</div>
	</div>

	<div id="floating-save" style="display:none;">
		<div class="transparent-background"></div>
		<div><button class="right btn btn-info" data-bind="click: saveMeriPlan">Save changes</button></div>
	</div>
</div>