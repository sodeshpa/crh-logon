/**
 * By default, after an element is updated, Ext loads javascripts asynchronously.
 * It causes the problem when the loading order is taken into account, see: LOGON-9047
 * --> Change it to synchronously load and execute a script resource, and that is using a synchronous XHR.
 * Note: this file should be imported after ext-base.js, ext-all.js
 */
Ext.Element.prototype.loadScriptSynchronously = function(url) {
	var xhrObj = new XMLHttpRequest();
	// open and send a synchronous request
	xhrObj.open('GET', url, false);
	xhrObj.send('');
	// add the returned content to a newly created script tag
	var se = document.createElement('script');
	se.type = "text/javascript";
	se.src = url;
	document.getElementsByTagName('head')[0].appendChild(se);
};

Ext.Element.prototype.update = function(html, loadScripts, callback) {
	var self = this;
    if(typeof html == "undefined"){
        html = "";
    }
    if(loadScripts !== true){
        this.dom.innerHTML = html;
        if(typeof callback == "function"){
            callback();
        }
        return this;
    }
    var id = Ext.id();
    var dom = this.dom;

    html += '<span id="' + id + '"></span>';

    Ext.lib.Event.onAvailable(id, function(){
        var hd = document.getElementsByTagName("head")[0];
        var re = /(?:<script([^>]*)?>)((\n|\r|.)*?)(?:<\/script>)/ig;
        var srcRe = /\ssrc=([\'\"])(.*?)\1/i;
        var typeRe = /\stype=([\'\"])(.*?)\1/i;

        var match;
        while(match = re.exec(html)){
            var attrs = match[1];
            var srcMatch = attrs ? attrs.match(srcRe) : false;
            if(srcMatch && srcMatch[2]){
               self.loadScriptSynchronously(srcMatch[2]);
            }else if(match[2] && match[2].length > 0){
                if(window.execScript) {
                   window.execScript(match[2]);
                } else {
                   window.eval(match[2]);
                }
            }
        }
        var el = document.getElementById(id);
        if(el){Ext.removeNode(el);}
        if(typeof callback == "function"){
            callback();
        }
    });
    dom.innerHTML = html.replace(/(?:<script.*?>)((\n|\r|.)*?)(?:<\/script>)/ig, "");
    return this;
};