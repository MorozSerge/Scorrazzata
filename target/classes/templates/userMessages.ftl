<#import "parts/common.ftl" as c>

<@c.page>
<h3>${userChannel.username}</h3>


<#if isCurrentUser>
<#include "parts/messEdit.ftl" />
</#if>

<#include "parts/messList.ftl" />
</@c.page>