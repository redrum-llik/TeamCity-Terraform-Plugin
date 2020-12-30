<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="bean" class="jetbrains.buildServer.runner.terraformRunner.TerraformBean"/>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<div class="parameter">
    Playbook: <props:displayValue name="${bean.playbookFileKey}" />
</div>

<div class="parameter">
    Inventory: <props:displayValue name="${bean.inventoryFileKey}" />
</div>

<div class="parameter">
    Additional arguments: <props:displayValue name="${bean.extraArgsKey}" />
</div>

<div class="parameter">
    Do a dry run: <props:displayCheckboxValue name="${bean.dryRunKey}" checkedValue="Yes" uncheckedValue="No"/>
</div>

<div class="parameter">
    Fail build on changes: <props:displayCheckboxValue name="${bean.failOnChangesKey}" checkedValue="Yes" uncheckedValue="No"/>
</div>

<div class="parameter">
    Use colored build: <props:displayCheckboxValue name="${bean.coloredBuildLogKey}" checkedValue="Yes" uncheckedValue="No"/>
</div>