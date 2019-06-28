import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';
import {FindDecorations} from 'monaco-editor/esm/vs/editor/contrib/find/findDecorations';

let style = document.createElement("link");
style.rel= "stylesheet";
style.href = "/harbor/assets/webpack/monaco.css";

document.head.append(style);

window.Monaco = monaco;
window.FindDecorations = FindDecorations;


