# About
innosoft-gallery aims to help produce an easily usable implementation of taking image from gallery and camera
<br/>
<br/>
<h2>Dependency</h2>
Add this in your root build.gradle file (not your module build.gradle file):
<br/>
<br/>
<pre><span class="pl-en">allprojects</span> {
	repositories {
        maven { url <span class="pl-s"><span class="pl-pds">"</span>https://jitpack.io<span class="pl-pds">"</span></span> }
    }
}</pre>
Then, add the library to your module build.gradle
<br/>
<br/>
<pre><span class="pl-en">dependencies</span> {
    implementation <span class="pl-s"><span class="pl-pds">'</span>com.github.maedilaziman:innosoft-service:1.0.0<span class="pl-pds">'</span></span>
}</pre>
<br/>
<h2>Features</h2>
- Taking image from gallery
<br/>
- Taking image straight from camera
<br/>
<br/>
<h2>Usage</h2>
You just call the class Gallery with request code for result,
and the request code must be 105.
<br/>
<pre>
<p>int req_gallery = 105; Intent intent = new Intent(f, Gallery.class); startActivityForResult(intent, req_gallery);</p><p>and then onActivityResult you just call this<br />switch (requestCode) {<br />&nbsp; &nbsp; &nbsp; case DataStatic.REQUEST_INTENT_GALLERY:<br />&nbsp; &nbsp; &nbsp; if (null != data)<br />&nbsp; &nbsp; &nbsp; {<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;String successData =&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; data.getStringExtra(DataStatic.successSubmitNewData);<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;if(null != successData) {<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; if (successData.equalsIgnoreCase("1")) {<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; String submitData = data.getStringExtra("data");<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Map m = new Gson().fromJson(submitData, Map.class);<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; for(Object o : m.keySet())<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; {<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; String val = (String) m.get(o);<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; String[] arrVal = val.split("\\|");<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Timber.d(TAG+"IMAGE_IS - "+arrVal[0]);<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; }<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; }<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;}<br />&nbsp; &nbsp; &nbsp; }<br />&nbsp; &nbsp; &nbsp; break;<br /> }</p></pre>
