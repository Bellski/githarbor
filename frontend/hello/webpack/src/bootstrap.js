import Vue from 'vue';
import {Tabs, TabPane, Input, Dialog, Button, Popover} from 'element-ui'
import {library} from '@fortawesome/fontawesome-svg-core';
import {FontAwesomeIcon} from '@fortawesome/vue-fontawesome';
import {
    faTrash,
    faSearch,
    faStar,
    faSpinner,
    faTimes,
    faHome,
    faCircle,
    faHeartbeat,
    faBox,
    faCog
} from '@fortawesome/free-solid-svg-icons';
import simplebar from 'simplebar-vue';


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
    faCog
);

let script = document.createElement("script");
script.type = "text/javascript";
script.src = "/hello/assets/webpack/gwt/hello.nocache.js";

document.head.appendChild(script);
