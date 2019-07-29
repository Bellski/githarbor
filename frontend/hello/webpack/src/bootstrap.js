import Vue from 'vue';
import {Tabs, TabPane, Input, Dialog, Button, Popover} from 'element-ui'

import {library} from '@fortawesome/fontawesome-svg-core';
import {FontAwesomeIcon} from '@fortawesome/vue-fontawesome';

import { faTrash } from '@fortawesome/free-solid-svg-icons/faTrash'
import { faSearch } from '@fortawesome/free-solid-svg-icons/faSearch'
import { faStar } from '@fortawesome/free-solid-svg-icons/faStar'
import { faSpinner } from '@fortawesome/free-solid-svg-icons/faSpinner'
import { faTimes } from '@fortawesome/free-solid-svg-icons/faTimes'
import { faHome } from '@fortawesome/free-solid-svg-icons/faHome'
import { faCircle } from '@fortawesome/free-solid-svg-icons/faCircle'
import { faHeartbeat } from '@fortawesome/free-solid-svg-icons/faHeartbeat'
import { faBox } from '@fortawesome/free-solid-svg-icons/faBox'
import { faCog } from '@fortawesome/free-solid-svg-icons/faCog'
import { faWindowRestore } from '@fortawesome/free-solid-svg-icons/faWindowRestore'
import { faWindowMaximize } from '@fortawesome/free-solid-svg-icons/faWindowMaximize'


import simplebar from 'simplebar-vue';
import {format} from 'timeago.js';

window.Vue = Vue;
Vue.prototype.$ELEMENT = {size: 'mini', zIndex: 3000};
Vue.component(Tabs.name, Tabs);
Vue.component(TabPane.name, TabPane);
Vue.component(Input.name, Input);
Vue.component(Dialog.name, Dialog);
Vue.component(Button.name, Button);
Vue.component(Popover.name, Popover);
Vue.component('fa-icon', FontAwesomeIcon);
Vue.component('simple-bar', simplebar);

library.add(
    faTrash,
    faSearch,
    faStar,
    faSpinner,
    faTimes,
    faHome,
    faCircle,
    faHeartbeat,
    faBox,
    faCog,
    faWindowRestore,
    faWindowMaximize
);

window.timeago = format;
window.kFormat = function kFormatter(num) {
    return Math.abs(num) > 999 ? Math.sign(num)*((Math.abs(num)/1000).toFixed(1)) + 'k' : Math.sign(num)*Math.abs(num)
};

let script = document.createElement("script");
script.type = "text/javascript";
script.src = "http://cdn.githarbor.com/hello/assets/webpack/gwt/hello.nocache.js";

document.head.appendChild(script);
