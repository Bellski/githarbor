import {library, dom} from '@fortawesome/fontawesome-svg-core';
import {faHeart } from '@fortawesome/free-solid-svg-icons/faHeart';
import { faPatreon } from '@fortawesome/free-brands-svg-icons/faPatreon';
import { faPaypal } from '@fortawesome/free-brands-svg-icons/faPaypal';
import { faGithub } from '@fortawesome/free-brands-svg-icons/faGithub';

library.add(faHeart, faPatreon, faPaypal, faGithub);

dom.watch();