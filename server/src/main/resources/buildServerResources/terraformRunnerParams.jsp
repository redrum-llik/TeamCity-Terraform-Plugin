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
        <th>Command:</th>
        <td>
            <props:selectProperty name="${bean.commandKey}" id="commandSelector" className="shortField" onchange="BS.Terraform.updateScriptType()">
                <props:option value="${bean.commandInit}">${bean.commandInit}</props:option>
                <props:option value="${bean.commandPlan}">${bean.commandPlan}</props:option>
                <props:option value="${bean.commandApply}">${bean.commandApply}</props:option>
            </props:selectProperty>
            <span class="error" id="error_${bean.commandKey}"></span>
        </td>
    </tr>
</l:settingsGroup>

<l:settingsGroup title="Run Parameters" className="advancedSetting">
</l:settingsGroup>

<script type="text/javascript">
    BS.Terraform = {
        updateScriptType : function() {
            var val = $('commandSelector').value;
            BS.MultilineProperties.updateVisible();
        }
    BS.Terraform.updateScriptType();
</script>