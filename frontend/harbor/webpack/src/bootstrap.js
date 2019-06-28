import Vue from 'vue';
import {
    Tabs,
    TabPane,
    Input,
    Dialog,
    Button,
    Popover,
    Select,
    Tree,
    Option,
    RadioGroup,
    RadioButton
} from 'element-ui'
import {library} from '@fortawesome/fontawesome-svg-core';
import {FontAwesomeIcon} from '@fortawesome/vue-fontawesome';
import pathToRegexp from 'path-to-regexp';
import {
    faTrash,
    faSearch,
    faStar,
    faCog,
    faTimes,
    faHome,
    faCircle,
    faHeartbeat,
    faBox,
    faBoxes,
    faCodeBranch,
    faMinus,
    faHeart,
    faDotCircle,
    faHistory,
    faFolder,
    faWindowRestore,
    faWindowMaximize
} from '@fortawesome/free-solid-svg-icons';
import {faGithub, faGithubAlt} from '@fortawesome/free-brands-svg-icons';
import {faHeart as farHeart, faDotCircle as farDotCircle} from '@fortawesome/free-regular-svg-icons';
import simplebar from 'simplebar-vue';
import Splitpanes from 'splitpanes';

import pako from 'pako';
import marked from 'marked';

import {format} from 'timeago.js';


Vue.prototype.$ELEMENT = {size: 'mini', zIndex: 3000};
Vue.component(Tabs.name, Tabs);
Vue.component(TabPane.name, TabPane);
Vue.component(Input.name, Input);
Vue.component(Dialog.name, Dialog);
Vue.component(Button.name, Button);
Vue.component(Popover.name, Popover);
Vue.component(Select.name, Select);
Vue.component(Tree.name, Tree);
Vue.component(Option.name, Option);
Vue.component(RadioGroup.name, RadioGroup);
Vue.component(RadioButton.name, RadioButton);
Vue.component('fa-icon', FontAwesomeIcon);
Vue.component('simple-bar', simplebar);
Vue.component('split-panes', Splitpanes);

library.add(
    faTrash,
    faSearch,
    faStar,
    faCog,
    faTimes,
    faHome,
    faCircle,
    faHeartbeat,
    faBox,
    faGithub,
    faBoxes,
    faGithubAlt,
    faCodeBranch,
    faMinus,
    faHeart,
    farHeart,
    faDotCircle,
    farDotCircle,
    faHistory,
    faFolder,
    faWindowRestore,
    faWindowMaximize
);



window.Vue = Vue;
window.pathToRegexp = pathToRegexp;
window.pako = pako;
window.marked = marked;
window.kFormat = function kFormatter(num) {
    return Math.abs(num) > 999 ? Math.sign(num)*((Math.abs(num)/1000).toFixed(1)) + 'k' : Math.sign(num)*Math.abs(num)
};
window.timeago = format;
window.timeago2 = function ago(val) {
    val = 0 | (Date.now() - val) / 1000;
    let unit, length = { second: 60, minute: 60, hour: 24, day: 7, week: 4.35,
        month: 12, year: 10000 }, result;

    for (unit in length) {
        result = val % length[unit];
        if (!(val = 0 | val / length[unit]))
            return result + ' ' + (result-1 ? unit + 's' : unit);
    }
};

window.languages = {"KiCad Legacy Layout":["brd"],"Diff":["diff","patch"],"Lasso":["lasso","las","lasso8","lasso9","ldml"],"Pony":["pony"],"Erlang":["erl","appsrc","es","escript","hrl","xrl","yrl"],"Oxygene":["oxygene"],"Cirru":["cirru"],"CWeb":["w"],"ABAP":["abap"],"1C Enterprise":["bsl","os"],"LLVM":["ll"],"Public Key":["asc","pub"],"Verilog":["v","veo"],"Bison":["bison"],"Quake":[],"Gnuplot":["gp","gnu","gnuplot","plot","plt"],"Ecere Projects":["epj"],"MAXScript":["ms","mcr"],"Clojure":["clj","boot","cl2","cljc","cljs","cljshl","cljscm","cljx","hic"],"Smarty":["tpl"],"Smali":["smali"],"PowerShell":["ps1","psd1","psm1"],"Rouge":["rg"],"GAMS":["gms"],"Rust":["rs","rsin"],"Objective-C++":["mm"],"PLpgSQL":["sql"],"STON":["ston"],"GraphQL":["graphql","gql"],"RAML":["raml"],"Modelica":["mo"],"Blade":["blade","bladephp"],"JSX":["jsx"],"Rascal":["rsc"],"Elixir":["ex","exs"],"C#":["cs","cake","cshtml","csx"],"C":["c","cats","h","idc"],"D":["d","di"],"E":["E"],"Handlebars":["handlebars","hbs"],"J":["ijs"],"M":["mumps","m"],"Groovy Server Pages":["gsp"],"EJS":["ejs"],"R":["r","rd","rsx"],"BlitzBasic":["bb","decls"],"sed":["sed"],"Cap\u0027n Proto":["capnp"],"Genie":["gs"],"REXX":["rexx","pprx","rex"],"Game Maker Language":["gml"],"Haml":["haml","hamldeface"],"Zimpl":["zimpl","zmpl","zpl"],"G-code":["g","gco","gcode"],"JSON with Comments":["sublime-build","sublime-commands","sublime-completions","sublime-keymap","sublime-macro","sublime-menu","sublime-mousemap","sublime-project","sublime-settings","sublime-theme","sublime-workspace","sublime_metrics","sublime_session"],"Logos":["xm","x","xi"],"Latte":["latte"],"Red":["red","reds"],"Module Management System":["mms","mmk"],"q":["q"],"JSON5":["json5"],"X10":["x10"],"Isabelle":["thy"],"Ballerina":["bal"],"Creole":["creole"],"Pan":["pan"],"eC":["ec","eh"],"ActionScript":["as"],"Ada":["adb","ada","ads"],"Maven POM":[],"Elm":["elm"],"DM":["dm"],"Common Lisp":["lisp","asd","cl","l","lsp","ny","podsl","sexp"],"NumPy":["numpy","numpyw","numsc"],"Java Properties":["properties"],"X PixMap":["xpm","pm"],"Racket":["rkt","rktd","rktl","scrbl"],"SCSS":["scss"],"wisp":["wisp"],"RobotFramework":["robot"],"Brightscript":["brs"],"SQLPL":["sql","db2"],"F#":["fs","fsi","fsx"],"LookML":["lookml","modellkml","viewlkml"],"Thrift":["thrift"],"Turtle":["ttl"],"Alpine Abuild":[],"PureScript":["purs"],"F*":["fst"],"EML":["eml","mbox"],"Stata":["do","ado","doh","ihlp","mata","matah","sthlp"],"HTTP":["http"],"Golo":["golo"],"ApacheConf":["apacheconf","vhost"],"EQ":["eq"],"PAWN":["pwn","inc"],"Cuda":["cu","cuh"],"Mirah":["druby","duby","mirah"],"Meson":[],"YASnippet":["yasnippet"],"Gentoo Eclass":["eclass"],"Matlab":["matlab","m"],"Filterscript":["fs"],"KiCad Schematic":["sch"],"TLA":["tla"],"JSON":["json","avsc","geojson","gltf","JSON-tmLanguage","jsonl","tfstate","tfstatebackup","topojson","webapp","webmanifest","yy","yyp"],"AGS Script":["asc","ash"],"Myghty":["myt"],"Processing":["pde"],"CLIPS":["clp"],"desktop":["desktop","desktopin"],"Visual Basic":["vb","bas","cls","frm","frx","vba","vbhtml","vbs"],"Smalltalk":["st","cs"],"Nearley":["ne","nearley"],"Forth":["fth","4th","f","for","forth","fr","frt","fs"],"Uno":["uno"],"Mathematica":["mathematica","cdf","m","ma","mt","nb","nbp","wl","wlt"],"POV-Ray SDL":["pov","inc"],"C++":["cpp","c++","cc","cp","cxx","h","h++","hh","hpp","hxx","inc","inl","ino","ipp","re","tcc","tpp"],"GCC Machine Description":["md"],"GN":["gn","gni"],"Csound Document":["csd"],"Alloy":["als"],"RPM Spec":["spec"],"WebIDL":["webidl"],"XPages":["xsp-config","xspmetadata"],"C2hs Haskell":["chs"],"SRecode Template":["srt"],"Filebench WML":["f"],"Ioke":["ik"],"Tcsh":["tcsh","csh"],"Jasmin":["j"],"DNS Zone":["zone","arpa"],"Go":["go"],"Pike":["pike","pmod"],"Glyph":["glf"],"Harbour":["hb"],"Haskell":["hs","hsc"],"Gerber Image":["gbr","gbl","gbo","gbp","gbs","gko","gpb","gpt","gtl","gto","gtp","gts"],"Web Ontology Language":["owl"],"M4Sugar":["m4"],"Perl 6":["6pl","6pm","nqp","p6","p6l","p6m","pl","pl6","pm","pm6","t"],"Xtend":["xtend"],"Hy":["hy"],"NCL":["ncl"],"Python traceback":["pytb"],"Swift":["swift"],"X BitMap":["xbm"],"SMT":["smt2","smt"],"Graphviz (DOT)":["dot","gv"],"LiveScript":["ls","_ls"],"Terra":["t"],"Max":["maxpat","maxhelp","maxproj","mxt","pat"],"QMake":["pro","pri"],"Io":["io"],"Java Server Pages":["jsp"],"Gosu":["gs","gst","gsx","vark"],"Jupyter Notebook":["ipynb"],"Dogescript":["djs"],"AutoHotkey":["ahk","ahkl"],"Clarion":["clw"],"PHP":["php","aw","ctp","fcgi","inc","php3","php4","php5","phps","phpt"],"Apex":["cls"],"Coq":["coq","v"],"Vue":["vue"],"AngelScript":["as","angelscript"],"Sass":["sass"],"Text":["txt","fr","nb","ncl","no"],"Pic":["pic","chem"],"BitBake":["bb"],"Genshi":["kid"],"Julia":["jl"],"HiveQL":["q"],"COLLADA":["dae"],"Slim":["slim"],"OpenCL":["cl","opencl"],"TOML":["toml"],"Puppet":["pp"],"LoomScript":["ls"],"Fantom":["fan"],"Textile":["textile"],"PicoLisp":["l"],"XQuery":["xquery","xq","xql","xqm","xqy"],"M4":["m4"],"ABNF":["abnf"],"NSIS":["nsi","nsh"],"Adobe Font Metrics":["afm"],"Fortran":["f90","f","f03","f08","f77","f95","for","fpp"],"Scaml":["scaml"],"CoNLL-U":["conllu","conll"],"Befunge":["befunge"],"Factor":["factor"],"Linux Kernel Module":["mod"],"Slash":["sl"],"Less":["less"],"Edje Data Collection":["edc"],"Cpp-ObjDump":["cppobjdump","c++-objdump","c++objdump","cpp-objdump","cxx-objdump"],"Yacc":["y","yacc","yy"],"MTML":["mtml"],"Boo":["boo"],"PogoScript":["pogo"],"Vala":["vala","vapi"],"SQF":["sqf","hqf"],"Turing":["t","tu"],"SQL":["sql","cql","ddl","inc","mysql","prc","tab","udf","viw"],"Bluespec":["bsv"],"QML":["qml","qbs"],"Windows Registry Entries":["reg"],"Scilab":["sci","sce","tst"],"SubRip Text":["srt"],"Gherkin":["feature"],"Slice":["ice"],"Zephir":["zep"],"NL":["nl"],"Literate Agda":["lagda"],"RPC":["x"],"HTML+Django":["jinja","jinja2","mustache","njk"],"Opal":["opal"],"FLUX":["fx","flux"],"Easybuild":["eb"],"Clean":["icl","dcl"],"NetLogo":["nlogo"],"TypeScript":["ts","tsx"],"xBase":["prg","ch","prw"],"SugarSS":["sss"],"Ant Build System":[],"Ruby":["rb","builder","eye","fcgi","gemspec","god","jbuilder","mspec","pluginspec","podspec","rabl","rake","rbuild","rbw","rbx","ru","ruby","spec","thor","watchr"],"HTML+ERB":["erb","erbdeface"],"Cython":["pyx","pxd","pxi"],"GLSL":["glsl","fp","frag","frg","fs","fsh","fshader","geo","geom","glslv","gshader","shader","tesc","tese","vert","vrx","vsh","vshader"],"Brainfuck":["b","bf"],"CSS":["css"],"P4":["p4"],"Haxe":["hx","hxsl"],"PowerBuilder":["pbt","sra","sru","srw"],"Scala":["scala","kojo","sbt","sc"],"Nu":["nu"],"CSV":["csv"],"AppleScript":["applescript","scpt"],"BlitzMax":["bmx"],"XSLT":["xslt","xsl"],"SaltStack":["sls"],"Closure Templates":["soy"],"HTML+PHP":["phtml"],"Nim":["nim","nimrod"],"Nemerle":["n"],"Linker Script":["ld","lds","x"],"Nit":["nit"],"Modula-3":["i3","ig","m3","mg"],"Shen":["shen"],"Vim script":["vim"],"Modula-2":["mod"],"Nix":["nix"],"APL":["apl","dyalog"],"Lex":["l","lex"],"Bro":["bro"],"REALbasic":["rbbas","rbfrm","rbmnu","rbres","rbtbar","rbuistate"],"ShaderLab":["shader"],"Nextflow":["nf"],"Volt":["volt"],"Ox":["ox","oxh","oxo"],"LFE":["lfe"],"Oz":["oz"],"PureBasic":["pb","pbi"],"API Blueprint":["apib"],"nesC":["nc"],"Redcode":["cw"],"Ninja":["ninja"],"OpenSCAD":["scad"],"Pascal":["pas","dfm","dpr","inc","lpr","pascal","pp"],"Unity3D Asset":["anim","asset","mat","meta","prefab","unity"],"Eagle":["sch","brd"],"D-ObjDump":["d-objdump"],"CartoCSS":["mss"],"NetLinx":["axs","axi"],"Pod":["pod"],"Jison Lex":["jisonlex"],"MQL4":["mq4","mqh"],"MQL5":["mq5","mqh"],"Ragel":["rl"],"Protocol Buffer":["proto"],"SuperCollider":["sc","scd"],"Idris":["idr","lidr"],"Arc":["arc"],"ANTLR":["g4"],"Squirrel":["nut"],"Mako":["mako","mao"],"Charity":["ch"],"Rebol":["reb","r","r2","r3","rebol"],"SVG":["svg"],"Isabelle ROOT":[],"Monkey":["monkey","monkey2"],"Type Language":["tl"],"TXL":["txl"],"Stan":["stan"],"CSON":["cson"],"Solidity":[],"RHTML":["rhtml"],"Gettext Catalog":["po","pot"],"ASP":["asp","asax","ascx","ashx","asmx","aspx","axd"],"Java":["java"],"COBOL":["cob","cbl","ccp","cobol","cpy"],"Augeas":["aug"],"SystemVerilog":["sv","svh","vh"],"Csound":["orc","udo"],"Logtalk":["lgt","logtalk"],"Ring":["ring"],"fish":["fish"],"HLSL":["hlsl","cginc","fx","fxh","hlsli"],"ATS":["dats","hats","sats"],"Opa":["opa"],"ooc":["ooc"],"Scheme":["scm","sch","sld","sls","sps","ss"],"Click":["click"],"Metal":["metal"],"Cloud Firestore Security Rules":[],"HXML":["hxml"],"YAML":["yml","mir","reek","rviz","sublime-syntax","syntax","yaml","yaml-tmlanguage","ymlmysql"],"IDL":["pro","dlm"],"VHDL":["vhdl","vhd","vhf","vhi","vho","vhs","vht","vhw"],"Parrot Assembly":["pasm"],"Csound Score":["sco"],"MediaWiki":["mediawiki","wiki"],"LilyPond":["ly","ily"],"JSONLD":["jsonld"],"Fancy":["fy","fancypack"],"OpenRC runscript":[],"ASN1":["asn","asn1"],"Moocode":["moo"],"Propeller Spin":["spin"],"Regular Expression":["regexp","regex"],"HCL":["hcl","tf","tfvars"],"OpenEdge ABL":["p","cls","w"],"EmberScript":["em","emberscript"],"Batchfile":["bat","cmd"],"Xojo":["xojo_code","xojo_menu","xojo_report","xojo_script","xojo_toolbar","xojo_window"],"Kit":["kit"],"GAP":["g","gap","gd","gi","tst"],"Pep8":["pep"],"PLSQL":["pls","bdy","ddl","fnc","pck","pkb","pks","plb","plsql","prc","spc","sql","tpb","tps","trg","vw"],"Glyph Bitmap Distribution Format":["bdf"],"HyPhy":["bf"],"Crystal":["cr"],"MiniD":["minid"],"PostCSS":["pcss"],"Org":["org"],"Makefile":["mak","d","make","mk","mkfile"],"DIGITAL Command Language":["com"],"AsciiDoc":["asciidoc","adoc","asc"],"Grammatical Framework":["gf"],"AutoIt":["au3"],"DataWeave":["dwl"],"Stylus":["styl"],"Shell":["sh","bash","bats","cgi","command","fcgi","ksh","shin","tmux","tool","zsh"],"Awk":["awk","auk","gawk","mawk","nawk"],"JavaScript":["js","_js","bones","es","es6","frag","gs","jake","jsb","jscad","jsfl","jsm","jss","mjs","njs","pac","sjs","ssjs","xsjs","xsjslib"],"PigLatin":["pig"],"Limbo":["b","m"],"Literate CoffeeScript":["litcoffee"],"Pug":["jade","pug"],"ECLiPSe":["ecl"],"YANG":["yang"],"XC":["xc"],"Cycript":["cy"],"JSONiq":["jq"],"Liquid":["liquid"],"CoffeeScript":["coffee","_coffee","cake","cjsx","iced"],"Python console":[],"XS":["xs"],"Mercury":["m","moo"],"LabVIEW":["lvproj"],"KiCad Layout":["kicad_pcb","kicad_mod","kicad_wks"],"Nginx":["nginxconf","vhost"],"Pickle":["pkl"],"GDB":["gdb","gdbinit"],"CMake":["cmake","cmakein"],"NewLisp":["nl","lisp","lsp"],"IRC log":["irclog","weechatlog"],"OCaml":["ml","eliom","eliomi","ml4","mli","mll","mly"],"NetLinx+ERB":["axserb","axierb"],"Literate Haskell":["lhs"],"RMarkdown":["rmd"],"IGOR Pro":["ipf"],"HTML+ECR":["ecr"],"YARA":["yar","yara"],"Twig":["twig"],"FIGlet Font":["flf"],"Chapel":["chpl"],"Mask":["mask"],"Perl":["pl","al","cgi","fcgi","perl","ph","plx","pm","psgi","t"],"X Font Directory Index":[],"XProc":["xpl","xproc"],"UnrealScript":["uc"],"Component Pascal":["cp","cps"],"RenderScript":["rs","rsh"],"Dylan":["dylan","dyl","intr","lid"],"TI Program":["8xp","8xk","8xktxt","8xptxt"],"EBNF":["ebnf"],"AMPL":["ampl","mod"],"ChucK":["ck"],"wdl":["wdl"],"VCL":["vcl"],"Objective-J":["j","sj"],"Jolie":["ol","iol"],"WebAssembly":["wast","wat"],"Marko":["marko"],"Unix Assembly":["s","ms"],"Cool":["cl"],"Self":["self"],"OpenType Feature File":["fea"],"Graph Modeling Language":["gml"],"HTML+EEX":["eex"],"Darcs Patch":["darcspatch","dpatch"],"ObjDump":["objdump"],"JFlex":["flex","jflex"],"Objective-C":["m","h"],"C-ObjDump":["c-objdump"],"Agda":["agda"],"XCompose":[],"World of Warcraft Addon Data":["toc"],"Hack":["hh","php"],"Prolog":["pl","pro","prolog","yap"],"Common Workflow Language":["cwl"],"Kotlin":["kt","ktm","kts"],"ECL":["ecl","eclxml"],"reStructuredText":["rst","rest","resttxt","rsttxt"],"Wavefront Object":["obj"],"Parrot Internal Representation":["pir"],"Omgrofl":["omgrofl"],"Grace":["grace"],"ShellSession":["sh-session"],"HTML":["html","htm","htmlhl","inc","st","xht","xhtml"],"Groovy":["groovy","grt","gtpl","gvy"],"Apollo Guidance Computer":["agc"],"Spline Font Database":["sfd"],"Jison":["jison"],"Frege":["fr"],"Ren\u0027Py":["rpy"],"Markdown":["md","markdown","mdown","mdwn","mkd","mkdn","mkdown","ronn","workbook"],"Eiffel":["e"],"Assembly":["asm","a51","inc","nasm"],"Python":["py","bzl","cgi","fcgi","gyp","gypi","lmi","py3","pyde","pyi","pyp","pyt","pyw","rpy","spec","tac","wsgi","xpy"],"MUF":["muf","m"],"RDoc":["rdoc"],"Lean":["lean","hlean"],"LSL":["lsl","lslp"],"Parrot":["parrot"],"Ceylon":["ceylon"],"Gentoo Ebuild":["ebuild"],"Papyrus":["psc"],"edn":["edn"],"UrWeb":["ur","urs"],"RUNOFF":["rnh","rno"],"Tcl":["tcl","adp","tm"],"Dart":["dart"],"DTrace":["d"],"SPARQL":["sparql","rq"],"Standard ML":["ML","fun","sig","sml"],"SAS":["sas"],"KRL":["krl"],"ColdFusion":["cfm","cfml"],"INI":["ini","cfg","lektorproject","prefs","pro","properties"],"Inno Setup":["iss"],"FreeMarker":["ftl"],"Reason":["re","rei"],"PostScript":["ps","eps","pfa"],"MoonScript":["moon"],"ColdFusion CFC":["cfc"],"Lua":["lua","fcgi","nse","p8","pd_lua","rbxs","wlua"],"Unified Parallel C":["upc"],"mupad":["mu"],"Pure Data":["pd"],"AspectJ":["aj"],"Emacs Lisp":["el","emacs","emacsdesktop"],"LOLCODE":["lol"],"Inform 7":["ni","i7x"],"TeX":["tex","aux","bbx","bib","cbx","cls","dtx","ins","lbx","ltx","mkii","mkiv","mkvi","sty","toc"],"Sage":["sage","sagews"],"GDScript":["gd"],"Raw token data":["raw"],"Formatted":["for","eamfs"],"Tea":["tea"],"Dockerfile":["dockerfile"],"SourcePawn":["sp","inc","sma"],"Wavefront Material":["mtl"],"XML":["xml","adml","admx","ant","axml","builds","ccproj","ccxml","clixml","cproject","cscfg","csdef","csl","csproj","ct","depproj","dita","ditamap","ditaval","dllconfig","dotsettings","filters","fsproj","fxml","glade","gml","gmx","grxml","iml","ivy","jelly","jsproj","kml","launch","mdpolicy","mjml","mm","mod","mxml","natvis","ncl","ndproj","nproj","nuspec","odd","osm","pkgproj","plist","pluginspec","proj","props","ps1xml","psc1","pt","rdf","resx","rss","sch","scxml","sfproj","shproj","srdf","storyboard","stTheme","sublime-snippet","targets","tmCommand","tml","tmLanguage","tmPreferences","tmSnippet","tmTheme","ts","tsx","ui","urdf","ux","vbproj","vcxproj","vsixmanifest","vssettings","vstemplate","vxml","wixproj","wsdl","wsf","wxi","wxl","wxs","x3d","xacro","xaml","xib","xlf","xliff","xmi","xmldist","xproj","xsd","xspec","xul","zcml"],"Roff":["roff","1","1in","1m","1x","2","3","3in","3m","3qt","3x","4","5","6","7","8","9","l","man","mdoc","me","ms","n","nr","rno","tmac"],"Gradle":["gradle"]}

let script = document.createElement("script");
script.type = "text/javascript";
script.src = "/harbor/assets/webpack/gwt/harbor.nocache.js";

document.head.appendChild(script);


window.highlight = function highlight(query, content) {
    query = query.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); //https://stackoverflow.com/questions/3446170/escape-string-for-use-in-javascript-regex

    let re = new RegExp(query, 'ig');

    if (query.length > 0) {
        return content.replace(re, `<mark>$&</mark>`);
    }

    return content;
};