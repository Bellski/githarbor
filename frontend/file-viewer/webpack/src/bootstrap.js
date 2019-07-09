import Vue from 'vue';
import { Button } from 'element-ui'
import {library} from '@fortawesome/fontawesome-svg-core';
import {FontAwesomeIcon} from '@fortawesome/vue-fontawesome';
import pathToRegexp from 'path-to-regexp';

import {faCog} from '@fortawesome/free-solid-svg-icons';

import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';

Vue.prototype.$ELEMENT = {size: 'mini', zIndex: 3000};
Vue.component('fa-icon', FontAwesomeIcon);
Vue.component(Button.name, Button);

library.add(faCog);

window.Vue = Vue;
window.pathToRegexp = pathToRegexp;
window.Monaco = monaco;

let script = document.createElement("script");
script.type = "text/javascript";
script.src = "/file-viewer/assets/webpack/gwt/file_viewer.nocache.js";

document.head.appendChild(script);
