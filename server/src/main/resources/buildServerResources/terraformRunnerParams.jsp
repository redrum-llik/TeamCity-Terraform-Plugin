<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="bean" class="jetbrains.buildServer.runner.terraformRunner.TerraformBean"/>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<forms:workingDirectory/>

<l:settingsGroup title="Terraform Parameters">
    <tr>
        <th>Version:</th>
        <td>
            <props:selectProperty name="${bean.versionKey}" id="versionSelector" className="shortField" onchange="BS.Terraform.updateVersion()">
                <props:option value="${bean.versionAuto}">${bean.versionAuto}</props:option>
                <props:option value="${bean.versionTFEnv}">${bean.versionTFEnv}</props:option>
            </props:selectProperty>
            <span class="error" id="error_${bean.versionKey}"></span>
        </td>
    </tr>
    <tr id="tfenv_version">
        <th><label for="${bean.versionToFetch}">Version to fetch:</label></th>
        <td>
            <props:textProperty name="${bean.versionToFetch}" className="longField"/>
            <bs:vcsTree fieldId="${bean.versionToFetch}"/>
            <span class="smallNote">Terraform version to be fetched via tfenv</span>
        </td>
    </tr>
    <tr>
        <th>Command:</th>
        <td>
            <props:selectProperty name="${bean.commandKey}" id="commandSelector" className="shortField" onchange="BS.Terraform.updateCommand()">
                <props:option value="${bean.commandInit}">${bean.commandInit}</props:option>
                <props:option value="${bean.commandPlan}">${bean.commandPlan}</props:option>
                <props:option value="${bean.commandApply}">${bean.commandApply}</props:option>
            </props:selectProperty>
            <span class="error" id="error_${bean.commandKey}"></span>
        </td>
    </tr>
    <tr id="plan_custom_output">
        <th><label for="${bean.planCustomOutputPathKey}">Custom output path:</label></th>
        <td>
            <props:textProperty name="${bean.planCustomOutputPathKey}" className="longField"/>
            <bs:vcsTree fieldId="${bean.planCustomOutputPathKey}"/>
            <span class="smallNote">Custom path to the generated plan execution file, absolute or relative to the working directory</span>
        </td>
    </tr>
    <tr class="advancedSetting" id="plan_do_init">
        <th><label>Init:</label></th>
        <td><props:checkboxProperty name="${bean.planDoInitKey}"/>
            <label for="${bean.planDoInitKey}">Run "terraform init" command beforehand</label>
            <br/>
        </td>
    </tr>
    <tr class="advancedSetting" id="plan_do_destroy">
        <th><label>Destroy:</label></th>
        <td><props:checkboxProperty name="${bean.planDoDestroyKey}"/>
            <label for="${bean.planDoDestroyKey}">Generate plan to destroy resources</label>
            <br/>
        </td>
    </tr>
    <tr id="apply_custom_backup">
        <th><label for="${bean.applyCustomBackupPathKey}">Custom backup path:</label></th>
        <td>
            <props:textProperty name="${bean.applyCustomBackupPathKey}" className="longField"/>
            <bs:vcsTree fieldId="${bean.applyCustomBackupPathKey}"/>
            <span class="smallNote">Custom path to the backup state file, absolute or relative to the working directory</span>
        </td>
    </tr>
    <tr class="advancedSetting" id="apply_do_auto_approve">
        <th><label>Auto-approve:</label></th>
        <td><props:checkboxProperty name="${bean.applyDoAutoApproveKey}"/>
            <label for="${bean.applyDoAutoApproveKey}">Skip interactive approval before applying</label>
            <br/>
        </td>
    </tr>
</l:settingsGroup>

<l:settingsGroup title="Common Parameters" className="advancedSetting">
    <tr id="extra_args">
        <th><label for="${bean.extraArgsKey}">Additional arguments:</label></th>
        <td>
            <props:textProperty name="${bean.extraArgsKey}" className="longField"/>
            <span class="smallNote">Additional arguments to be passed to the command</span>
        </td>
    </tr>
    <tr class="advancedSetting" id="do_color">
        <th><label>Enable color:</label></th>
        <td><props:checkboxProperty name="${bean.doColorKey}"/>
            <label for="${bean.doColorKey}">Enable color codes in the command output</label>
            <br/>
        </td>
    </tr>
</l:settingsGroup>

<script type="text/javascript">
    BS.Terraform = {
        updateCommand: function () {
            var val = $('commandSelector').value;
            if (val === "${bean.commandInit}") {
                this.hideApplyControls()
                this.hidePlanControls()
            }
            else if (val === "${bean.commandPlan}") {
                this.hideApplyControls()
                this.showPlanControls()
            }
            else {
                this.hidePlanControls()
                this.showApplyControls()
            }
            BS.MultilineProperties.updateVisible();
        },
        updateVersion: function () {
            var val = $('versionSelector').value;
            if (val === "${bean.versionAuto}") {
                BS.Util.hide($("tfenv_version"))
            }
            else {
                BS.Util.show($("tfenv_version"))
            }
        },
        showPlanControls: function () {
            BS.Util.show($("plan_custom_output"))
            BS.Util.show($("plan_do_init"))
            BS.Util.show($("plan_do_destroy"))
        },
        hidePlanControls: function () {
            BS.Util.hide($("plan_custom_output"))
            BS.Util.hide($("plan_do_init"))
            BS.Util.hide($("plan_do_destroy"))
        },
        showApplyControls: function () {
            BS.Util.show($("apply_custom_backup"))
            BS.Util.show($("apply_do_auto_approve"))
        },
        hideApplyControls: function () {
            BS.Util.hide($("apply_custom_backup"))
            BS.Util.hide($("apply_do_auto_approve"))
        }
    }
    BS.Terraform.updateCommand();
    BS.Terraform.updateVersion();
</script>