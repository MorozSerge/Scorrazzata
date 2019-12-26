<#include "security.ftl">

<div class="card-columns">
    <#list messages as message>
         <div class="card my-3" >
             <div>
                 <#if message.filename??>
                     <img class="card-img-top" src="/img/${message.filename}">
                </#if>
            <div class="m-2">
                <span>${message.text}</span><br/>
                <i>#${message.tag}</i>
           </div>
             <div class="card-footer text-muted">
                 <a href = "/user-messages/${message.author.id}"> ${(message.author.username)!"&lt;none&gt;"}</a>
                 <#if message.author.id == currentUserId>
                 <a href = "/user-messages/${message.author.id}?message=${message.id}">
                     <button type="button" class="btn btn-primary">Edit</button>
                 </a>
             </#if>

             </div>

         </div>
    </div>
    <#else>
        <p>No message</p>
    </#list>
</div>