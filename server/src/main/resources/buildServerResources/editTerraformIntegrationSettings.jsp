<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>

<jsp:useBean id="useTfEnvBean" class="jetbrains.buildServer.terraformSupportPlugin.beans.UseTfEnvBean"/>
<jsp:useBean id="tfEnvVersionBean" class="jetbrains.buildServer.terraformSupportPlugin.beans.TfEnvVersionBean"/>
<jsp:useBean id="targetTerraformVersionBean" class="jetbrains.buildServer.terraformSupportPlugin.beans.TargetTerraformVersionBean"/>
<jsp:useBean id="systemPropertiesBean" class="jetbrains.buildServer.terraformSupportPlugin.beans.SystemPropertiesBean"/>

<tr>
    <td colspan="2">
        <em>Allows to initialize Terraform at the start of build and pass its output to the build results data.</em>
    </td>
</tr>

<l:settingsGroup title="tfEnv Parameters">

    <tr class="advancedSetting" id="use_tf_env">
        <th><label>${useTfEnvBean.label}</label></th>
        <td><props:checkboxProperty name="${useTfEnvBean.key}" onclick="BS.Terraform.updateTfEnvControls()"/>
        </td>
    </tr>

    <tr id="tf_env_tool">
        <th>${tfEnvVersionBean.label}</th>
        <td>
            <jsp:include page="/tools/selector.html?toolType=${tfEnvVersionBean.toolKey}&versionParameterName=${tfEnvVersionBean.key}&class=longField"/>
        </td>
    </tr>

    <tr class="advancedSetting" id="terraform_version">
        <th><label for="${targetTerraformVersionBean.key}">${targetTerraformVersionBean.label}</label></th>
        <td>
            <props:textProperty name="${targetTerraformVersionBean.key}" className="longField"/>
            <span class="smallNote">${targetTerraformVersionBean.description}</span>
        </td>
    </tr>

</l:settingsGroup>

<l:settingsGroup title="General Parameters">

    <tr class="noBorder">
        <th><label for="${systemPropertiesBean.key}">${systemPropertiesBean.label}</label></th>
        <td>
            <props:textProperty name="${systemPropertiesBean.key}" className="longField"/>
            <span class="smallNote">${systemPropertiesBean.description}</span>
        </td>
    </tr>

</l:settingsGroup>

<script type="text/javascript">
    BS.Terraform = {
        updateWorkspaceControls: function () {
            const val = $('useWorkspace').value;
            if (val == null || val.empty()) {
                BS.Util.hide($("create_workspace_if_not_found"))
            }
            else {
                BS.Util.show($("create_workspace_if_not_found"))
            }
        },
        updateTfEnvControls: function () {
            const val = $('useTfEnv').checked;
            if (val !== false) {
                BS.Util.show($("tf_env_tool"))
                BS.Util.show($("terraform_version"))
            }
            else {
                BS.Util.hide($("tf_env_tool"))
                BS.Util.hide($("terraform_version"))
            }
        }
    }
    BS.Terraform.updateWorkspaceControls();
    BS.Terraform.updateTfEnvControls();
</script>