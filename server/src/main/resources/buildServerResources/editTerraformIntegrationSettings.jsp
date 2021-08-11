<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>

<jsp:useBean id="useTfEnvBean" class="jetbrains.buildServer.terraformSupportPlugin.beans.UseTfEnvBean"/>
<jsp:useBean id="tfEnvVersionBean" class="jetbrains.buildServer.terraformSupportPlugin.beans.TfEnvVersionBean"/>
<jsp:useBean id="targetTerraformVersionBean" class="jetbrains.buildServer.terraformSupportPlugin.beans.TargetTerraformVersionBean"/>
<jsp:useBean id="planFileBean" class="jetbrains.buildServer.terraformSupportPlugin.beans.PlanFileBean"/>
<jsp:useBean id="terraformWorkingDirectoryBean" class="jetbrains.buildServer.terraformSupportPlugin.beans.TerraformWorkingDirectoryBean"/>
<jsp:useBean id="updateBuildStatusBean" class="jetbrains.buildServer.terraformSupportPlugin.beans.UpdateBuildStatusBean"/>
<jsp:useBean id="protectedResourcesBean" class="jetbrains.buildServer.terraformSupportPlugin.beans.ProtectedResourcesBean"/>
<jsp:useBean id="systemPropertiesBean" class="jetbrains.buildServer.terraformSupportPlugin.beans.SystemPropertiesBean"/>

<tr>
    <td colspan="2">
        <em>Allows to initialize Terraform at the start of build and pass its output to the build results data.</em>
    </td>
</tr>

<l:settingsGroup title="tfEnv Parameters">

    <tr id="use_tf_env">
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

<l:settingsGroup title="Report Parameters">

    <tr id="plan_file_path">
        <th><label for="${planFileBean.key}">${planFileBean.label}</label></th>
        <td>
            <props:textProperty name="${planFileBean.key}" className="longField"/>
            <span class="smallNote">${planFileBean.description}</span>
        </td>
    </tr>

    <tr class="advancedSetting" id="tf_configuration_path">
        <th><label for="${terraformWorkingDirectoryBean.key}">${terraformWorkingDirectoryBean.label}</label></th>
        <td>
            <props:textProperty name="${terraformWorkingDirectoryBean.key}" className="longField"/>
            <span class="smallNote">${terraformWorkingDirectoryBean.description}</span>
        </td>
    </tr>

    <tr class="advancedSetting" id="update_build_status">
        <th><label>${updateBuildStatusBean.label}</label></th>
        <td>
            <props:checkboxProperty name="${updateBuildStatusBean.key}"/>
            <span class="smallNote">${updateBuildStatusBean.description}</span>
        </td>
    </tr>

    <tr class="advancedSetting" id="check_protected_resources">
        <th><label for="${protectedResourcesBean.key}">${protectedResourcesBean.label}</label></th>
        <td>
            <props:textProperty name="${protectedResourcesBean.key}" className="longField"/>
            <span class="smallNote">${protectedResourcesBean.description}</span>
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
    BS.Terraform.updateTfEnvControls();
</script>