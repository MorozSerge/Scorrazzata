<#import "parts/commonMap.ftl" as c>

<@c.page>

<style>
.coordinates {
background: rgba(0, 0, 0, 0.5);
color: #fff;
position: absolute;
bottom: 40px;
left: 10px;
padding: 5px 10px;
margin: 0;
font-size: 11px;
line-height: 18px;
border-radius: 3px;
display: none;
}
</style>

<a class="btn btn-primary" data-toggle="collapse" href="#collapse" role="button" aria-expanded="false" aria-controls="collapse">
    Set your starting point
</a>
<div class="collapse" id="collapse">
    <form method="post">
        <div id="map"></div>
        <pre id="coordinates" class="coordinates"></pre>
        <input type="hidden" name="_csrf" value="${_csrf.token}" />
        <button type="submit" class="btn btn-primary" name="set" value="true">Set</button>
    </form>
</div>

<script>
	mapboxgl.accessToken = 'pk.eyJ1IjoibW9yb3pzZXJnIiwiYSI6ImNrNGhzeGZrNDBrZXQzam56dXR6YTg4cWIifQ.-xdov9W2AWNqEPtxfm6e2g';
var coordinates = document.getElementById('coordinates');

var map = new mapboxgl.Map({
container: 'map',
style: 'mapbox://styles/mapbox/streets-v11',
center: [30.32, 59.93],
zoom: 9
});

var marker = new mapboxgl.Marker({
draggable: true
})
.setLngLat([30.32, 59.93])
.addTo(map);

function onDragEnd() {
var lngLat = marker.getLngLat();
coordinates.style.display = 'block';
coordinates.innerHTML =
'Longitude: ' + lngLat.lng + '<br />Latitude: ' + lngLat.lat + '<input type="hidden" name="lon" value="' + lngLat.lng + '" /><input type="hidden" name="lat" value="' + lngLat.lat + '" />';
}
marker.on('dragend', onDragEnd);
</script>


<#if users??>
<div class="card-columns" >
    <#list users as user>
    <div class="card my-3" >
        <div>
        <div class="m-2">
            <a href = "/user-messages/${user.id}"> ${(user.username)}</a>
        </div>
</div>
</div>
<#else>
<p>No real sportsmen nearby( </p>
</#list>
</div>
</#if>


<form method="post">
    <div>
        <label for="customRange2">Find in range</label>
        <input type="range" class="custom-range" min="100" max="2500" name ="some" step="50" id="customRange2">
        <input type="hidden" name="_csrf" value="${_csrf.token}" />
        <button type="submit" class="btn btn-primary">Find</button>
    </div>
</form>



</@c.page>