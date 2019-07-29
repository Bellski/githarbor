import Vue from 'vue';
import { Button } from 'element-ui'
import {library} from '@fortawesome/fontawesome-svg-core';
import {FontAwesomeIcon} from '@fortawesome/vue-fontawesome';
import pathToRegexp from 'path-to-regexp';

import {faCog} from '@fortawesome/free-solid-svg-icons/faCog';

import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';

Vue.prototype.$ELEMENT = {size: 'mini', zIndex: 3000};
Vue.component('fa-icon', FontAwesomeIcon);
Vue.component(Button.name, Button);

library.add(faCog);

window.Vue = Vue;
window.pathToRegexp = pathToRegexp;
window.Monaco = monaco;
window.MonacoEnvironment = {
    getWorkerUrl: function (moduleId, label) {
        if (label === 'json') return '/harbor/assets/webpack/json.worker.js';
        if (label === 'css') return '/harbor/assets/webpack/css.worker.js';
        if (label === 'html') return '/harbor/assets/webpack/html.worker.js';
        if (label === 'typescript' || label === 'javascript') return '/harbor/assets/webpack/typescript.worker.js';
        return '/harbor/assets/webpack/editor.worker.js';
    }
};


let script = document.createElement("script");
script.type = "text/javascript";
script.src = "http://cdn.githarbor.com/file-viewer/assets/webpack/gwt/file_viewer.nocache.js";

document.head.appendChild(script);
