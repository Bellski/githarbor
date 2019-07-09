import {library, dom} from '@fortawesome/fontawesome-svg-core';
import {faHeart } from '@fortawesome/free-solid-svg-icons/faHeart';
import { faPatreon } from '@fortawesome/free-brands-svg-icons/faPatreon';
import { faPaypal } from '@fortawesome/free-brands-svg-icons/faPaypal';

library.add(faHeart, faPatreon, faPaypal);

dom.watch();