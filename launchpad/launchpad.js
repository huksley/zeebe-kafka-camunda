window.defaultConfig = {
  versionTitle: "launchpad${prefix}",
  prefix: "dev"
};

if (!window.appConfig) {
  window.appConfig = {};
}

for (var k in defaultConfig) {
  if (appConfig[k] === undefined) {
    appConfig[k] = defaultConfig[k];
  }
}

applyExpression = function(tpl, data) {
    var re = /\$\{([^\}]+)?\}/g, match;
    while (match = re.exec(tpl)) {
        tpl = tpl.replace(match[0], data[match[1]])
    }
    return tpl;
}

/**
 * Given element (or jQuery expression) applies all config keys to elements with data-config="key" defined. 
 * Only first defined value will be replaced if multiple keys specified.
 * 
 * @example
 * // Will replace innerHTML with content:
 *  <element data-config="key,key2,...">Initial value</element>
 * @example  
 * // Will replace attribute attr with content:
 *  <element data-config="attr:key,..." attr="Initial value"/>
 */
applyConfig = function (el, handler) {
    var l = el.querySelectorAll("[data-config]");
    for (var i = 0; i < l.length; i++) {
        var e = l[i];
        var key = e.getAttribute("data-config");
        if (key.indexOf(",") > 0 || key.indexOf(":") > 0) {
            var keys = key.split(",");
            for (var j = 0; j < keys.length; j++) {
                var key = keys[j];
                if (key.indexOf(":") > 0) {
                    // attr:name
                    var attr = key.substring(0, key.indexOf(":"));
                    key = key.substring(key.indexOf(":") + 1);
                    if (applyKey(key, e, attr)) {
                        break;
                    }
                } else {
                    if (applyKey(key, e)) {
                        break;
                    }
                }
            }
        } else {
            applyKey(key, e);
        }
    }

    if (handler) {
      window.setTimeout(function () { handler(); }, 50);
    }
},

/**
 * Apply single key, either from configuration or from localStorage.
 * @private
 */
applyKey = function (key, e, attr) {
    key = key.trim();
    var success = false;
    if (window.storage && storage.localStorage.get(key) != undefined) {
        var val = storage.localStorage.get(key);
        val = applyExpression(val, appConfig);
        if (attr) {
             console.debug("Replacing " + key + " [@" + attr + "] = " + val);
             e.setAttribute(attr, val);
             success = true;
        } else {
             console.debug("Replacing " + key + " = " + val);
             e.innerHTML = val;
             success = true;
        }
    } else
    if (appConfig[key] != undefined && typeof appConfig[key] != "function" ) {
        var val = appConfig[key];
        val = applyExpression(val, appConfig);
        if (attr) {
             console.debug("Replacing " + key + " [@" + attr + "] = " + val);
             e.setAttribute(attr, val);
             success = true;
        } else {
             console.debug("Replacing " + key + " = " + val);
             e.innerHTML = val;
             success = true;
        }
    }
    return success;
}

document.addEventListener('DOMContentLoaded', function() {
  applyConfig(document.body);
})
