<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="additionalArgumentsBean" class="jetbrains.buildServer.runner.terraformRunner.beans.AdditionalArgumentsBean"/>
<jsp:useBean id="commandBean" class="jetbrains.buildServer.runner.terraformRunner.beans.CommandBean"/>
<jsp:useBean id="initStageBean" class="jetbrains.buildServer.runner.terraformRunner.beans.InitStageBean"/>
<jsp:useBean id="outputPathBean" class="jetbrains.buildServer.runner.terraformRunner.beans.OutputPathBean"/>
<jsp:useBean id="stateBackupPathBean" class="jetbrains.buildServer.runner.terraformRunner.beans.StateBackupPathBean"/>
<jsp:useBean id="passSystemPropertiesBean" class="jetbrains.buildServer.runner.terraformRunner.beans.PassSystemPropertiesBean"/>
<jsp:useBean id="versionBean" class="jetbrains.buildServer.runner.terraformRunner.beans.VersionBean"/>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<forms:workingDirectory/>

<l:settingsGroup title="Initialization Parameters">
    <tr>
        <th>${versionBean.label}</th>
        <td>
            <props:selectProperty name="${versionBean.key}" id="versionSelector" className="shortField" onchange="BS.Terraform.updateVersion()">
                <props:option value="${versionBean.autoDetectModeKey}">${versionBean.autoDetectModeValue}</props:option>
                <props:option value="${versionBean.tfEnvModeKey}">${versionBean.tfEnvModeValue}</props:option>
            </props:selectProperty>
            <span class="error" id="error_${versionBean.key}"></span>
        </td>
    </tr>
    <tr id="tfenv_version">
        <th><label for="${versionBean.tfEnvKey}">${versionBean.tfEnvLabel}</label></th>
        <td>
            <props:textProperty name="${versionBean.tfEnvKey}" className="longField"/>
            <bs:vcsTree fieldId="${versionBean.tfEnvKey}"/>
            <span class="smallNote">${versionBean.tfEnvDescription}</span>
        </td>
    </tr>
    <tr class="advancedSetting" id="do_init">
        <th><label>${initStageBean.doInitLabel}</label></th>
        <td><props:checkboxProperty name="${initStageBean.doInitKey}"/>
            <label for="${initStageBean.doInitKey}">${initStageBean.doInitDescription}</label>
            <br/>
        </td>
    </tr>
    <tr class="advancedSetting" id="use_workspace">
        <th><label for="${initStageBean.useWorkspaceKey}">${initStageBean.useWorkspaceLabel}</label></th>
        <td>
            <props:textProperty name="${initStageBean.useWorkspaceKey}" className="longField" onchange="BS.Terraform.updateWorkspaceControls()"/>
            <bs:vcsTree fieldId="${initStageBean.useWorkspaceKey}"/>
            <span class="smallNote">${initStageBean.useWorkspaceDescription}</span>
        </td>
    </tr>
    <tr class="advancedSetting" id="create_workspace_if_not_found">
        <th><label>${initStageBean.createWorkspaceIfNotFoundLabel}</label></th>
        <td><props:checkboxProperty name="${initStageBean.createWorkspaceIfNotFoundKey}"/>
            <label for="${initStageBean.createWorkspaceIfNotFoundKey}">${initStageBean.createWorkspaceIfNotFoundDescription}</label>
            <br/>
        </td>
    </tr>
</l:settingsGroup>

<l:settingsGroup title="Command Parameters">
    <tr>
        <th>${commandBean.label}</th>
        <td>
            <props:selectProperty name="${commandBean.key}" id="commandSelector" className="shortField" onchange="BS.Terraform.updateCommand()">
                <props:option value="${commandBean.planKey}">${commandBean.planValue}</props:option>
                <props:option value="${commandBean.applyKey}">${commandBean.applyValue}</props:option>
                <props:option value="${commandBean.customKey}">${commandBean.customValue}</props:option>
            </props:selectProperty>
            <span class="error" id="error_${commandBean.key}"></span>
        </td>
    </tr>
    <tr id="custom_command">
        <th><label for="${commandBean.customCommandKey}">${commandBean.customCommandLabel}</label></th>
        <td>
            <props:textProperty name="${commandBean.customCommandKey}" className="longField"/>
            <bs:vcsTree fieldId="${commandBean.customCommandKey}"/>
            <span class="smallNote">${commandBean.customCommandDescription}</span>
        </td>
    </tr>
    <tr id="plan_custom_output">
        <th><label for="${outputPathBean.key}">${outputPathBean.label}</label></th>
        <td>
            <props:textProperty name="${outputPathBean.key}" className="longField"/>
            <bs:vcsTree fieldId="${outputPathBean.key}"/>
            <span class="smallNote">${outputPathBean.description}</span>
        </td>
    </tr>
    <tr id="apply_custom_backup">
        <th><label for="${stateBackupPathBean.key}">${stateBackupPathBean.label}</label></th>
        <td>
            <props:textProperty name="${stateBackupPathBean.key}" className="longField"/>
            <bs:vcsTree fieldId="${stateBackupPathBean.key}"/>
            <span class="smallNote">${stateBackupPathBean.description}</span>
        </td>
    </tr>
    <tr id="extra_args">
        <th><label for="${additionalArgumentsBean.key}">${additionalArgumentsBean.label}</label></th>
        <td>
            <props:textProperty name="${additionalArgumentsBean.key}" className="longField"/>
            <span class="smallNote">${additionalArgumentsBean.description}</span>
        </td>
    </tr>
    <tr class="advancedSetting">
        <th><label>${passSystemPropertiesBean.label}</label></th>
        <td><props:checkboxProperty name="${passSystemPropertiesBean.key}"/>
            <label for="${passSystemPropertiesBean.key}">${passSystemPropertiesBean.description}</label>
            <br/>
        </td>
    </tr>
</l:settingsGroup>

<script type="text/javascript">
    BS.Terraform = {
        updateCommand: function () {
            var val = $('commandSelector').value;
            if (val === "${commandBean.applyKey}") {
                BS.Util.hide($("plan_custom_output"))
                BS.Util.hide($("custom_command"))
                BS.Util.show($("apply_custom_backup"))
            }
            else if (val === "${commandBean.planKey}") {
                BS.Util.hide($("apply_custom_backup"))
                BS.Util.hide($("custom_command"))
                BS.Util.show($("plan_custom_output"))
            }
            else {
                BS.Util.hide($("apply_custom_backup"))
                BS.Util.hide($("plan_custom_output"))
                BS.Util.show($("custom_command"))
            }
            BS.MultilineProperties.updateVisible();
        },
        updateVersion: function () {
            var val = $('versionSelector').value;
            if (val === "${versionBean.autoDetectModeKey}") {
                BS.Util.hide($("tfenv_version"))
            }
            else {
                BS.Util.show($("tfenv_version"))
            }
        },
        updateWorkspaceControls: function () {
            var val = $('useWorkspace').value;
            if (val == null || val.empty()) {
                BS.Util.hide($("create_workspace_if_not_found"))
            }
            else {
                BS.Util.show($("create_workspace_if_not_found"))
            }
        }
    }
    BS.Terraform.updateCommand();
    BS.Terraform.updateVersion();
    BS.Terraform.updateWorkspaceControls();
</script>