import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {Field} from 'redux-form';
import {LinearIcon} from 'components';
import './styles.less';

export default class IconSelect extends Component {
  static propTypes = {
    name: PropTypes.string,
    changeForm:PropTypes.func,
  };

  constructor(props){
    super(props);
    this.openIconSelect = this.openIconSelect.bind(this);
    this.handleIconSelectClose = this.handleIconSelectClose.bind(this);
    this.handleMaskClick = this.handleMaskClick.bind(this);
    this.state = {
      selectedIcon: "default",
      selectOpened: false,
    };

    this.iconSelect = this.iconSelect.bind(this);
  }

  openIconSelect() {
    this.setState({
      selectOpened: true,
    });
    document.addEventListener("click", this.handleMaskClick);
  }

  handleIconSelectClose() {
    this.setState({
      selectOpened: false,
    });
    document.removeEventListener("click", this.handleMaskClick);
  }

  handleMaskClick(event) {
    if(event.target.className !== "icons-container"){
      this.handleIconSelectClose();
    }
  }

  icons = ["home","home2","home3","home4","home5","home6","bathtub","toothbrush","bed","couch","chair","city","apartment","pencil","pencil2","pen","pencil3","eraser","pencil4","pencil5","feather","feather2","feather3","pen2","pen-add","pen-remove","vector","pen3","blog","brush","brush2","spray","paint-roller","stamp","tape","desk-tape","texture","eye-dropper","palette","color-sampler","bucket","gradient","gradient2","magic-wand","magnet","pencil-ruler","pencil-ruler2","compass","aim","gun","bottle","drop","drop-crossed","drop2","snow","snow2","fire","lighter","knife","dagger","tissue","toilet-paper","poop","umbrella","umbrella2","rain3","tornado","wind","fan","contrast","sun-small","sun","sun2","moon","cloud","cloud-upload","cloud-download","cloud-rain","cloud-hailstones","cloud-snow","cloud-windy","sun-wind","cloud-fog","cloud-sun","cloud-lightning","cloud-sync","cloud-lock","cloud-gear","cloud-alert","cloud-check","cloud-cross","cloud-crossed","cloud-database","database","database-add","database-remove","database-lock","database-refresh","database-check","database-history","database-upload","database-download","server","shield","shield-check","shield-alert","shield-cross","lock","rotation-lock","unlock","key","key-hole","toggle-off","toggle-on","cog","cog2","wrench","screwdriver","hammer-wrench","hammer","saw","axe","axe2","shovel","pickaxe","factory","factory2","recycle","trash","trash2","trash3","broom","game","gamepad","joystick","dice","spades","diamonds","clubs","hearts","heart","star","star-half","star-empty","flag","flag2","flag3","mailbox-full","mailbox-empty","at-sign","envelope","envelope-open","paperclip","paper-plane","reply","reply-all","inbox","inbox2","outbox","box","archive","archive2","drawers","drawers2","drawers3","eye","eye-crossed","eye-plus","eye-minus","binoculars","binoculars2","hdd","hdd-down","hdd-up","floppy-disk","disc","tape2","printer","shredder","file-empty","file-add","file-check","file-lock","files","copy","compare","folder","folder-search","folder-plus","folder-minus","folder-download","folder-upload","folder-star","folder-heart","folder-user","folder-shared","folder-music","folder-picture","folder-film","scissors","paste","clipboard-empty","clipboard-pencil","clipboard-text","clipboard-check","clipboard-down","clipboard-left","clipboard-alert","clipboard-user","register","enter","exit","papers","news","reading","typewriter","document","document2","graduation-hat","license","license2","medal-empty","medal-first","medal-second","medal-third","podium","trophy","trophy2","music-note","music-note2","music-note3","playlist","playlist-add","guitar","trumpet","album","shuffle","repeat-one","repeat","headphones","headset","loudspeaker","equalizer","theater","3d-glasses","ticket","presentation","play","film-play","clapboard-play","media","film","film2","surveillance","surveillance2","camera","camera-crossed","camera-play","time-lapse","record","camera2","camera-flip","panorama","time-lapse2","shutter","shutter2","face-detection","flare","convex","concave","picture","picture2","picture3","pictures","book","audio-book","book3","bookmark","bookmark2","label","library2","library3","contacts","profile","portrait","portrait2","user","user-plus","user-minus","user-lock","users","users2","users-plus","users-minus","group-work","woman","man","baby","baby2","baby3","baby-bottle","walk","hand-waving","jump","run","woman2","man2","man-woman","height","weight","scale2","button","bow-tie","tie","socks","shoe","shoes","hat","pants","shorts","flip-flops","shirt","hanger","laundry","store","haircut","store-24","barcode","barcode2","barcode3","cashier","bag","bag2","cart","cart-empty","cart-full","cart-plus","cart-plus2","cart-add","cart-remove","cart-exchange","tag","tags","receipt","wallet","credit-card","cash-dollar","cash-euro","cash-pound","cash-yen","bag-dollar","bag-euro","bag-pound","bag-yen","coin-dollar","coin-euro","coin-pound","coin-yen","calculator","calculator2","abacus","vault","telephone","phone-lock","phone-wave","phone-pause","phone-outgoing","phone-incoming","phone-in-out","phone-error","phone-sip","phone-plus","phone-minus","voicemail","dial","telephone2","pushpin","pushpin2","map-marker","map-marker-user","map-marker-down","map-marker-check","map-marker-crossed","radar","compass2","map","map2","location","road-sign","calendar-empty","calendar-check","calendar-cross","calendar-31","calendar-full","calendar-insert","calendar-text","calendar-user","mouse","mouse-left","mouse-right","mouse-both","keyboard","keyboard-up","keyboard-down","delete","spell-check","escape","enter2","screen","aspect-ratio","signal","signal-lock","signal-80","signal-60","signal-40","signal-20","signal-0","signal-blocked","sim","flash-memory","usb-drive","phone13","smartphone","smartphone-notification","smartphone-vibration","smartphone-embed","smartphone-waves","tablet","tablet2","laptop","laptop-phone","desktop","launch","new-tab","window","cable","cable2","tv","radio","remote-control","power-switch","power","power-crossed","flash-auto","lamp","flashlight","lampshade","cord","outlet","battery-power","battery-empty","battery-alert","battery-error","battery-low1","battery-low2","battery-low3","battery-mid1","battery-mid2","battery-mid3","battery-full","battery-charging","battery-charging2","battery-charging3","battery-charging4","battery-charging5","battery-charging6","battery-charging7","chip","chip-x64","chip-x86","bubble","bubbles","bubble-dots","bubble-alert","bubble-question","bubble-text","bubble-pencil","bubble-picture","bubble-video","bubble-user","bubble-quote","bubble-heart","bubble-emoticon","bubble-attachment","phone-bubble","quote-open","quote-close","dna","heart-pulse","pulse","syringe","pills","first-aid","lifebuoy","bandage","bandages","thermometer","microscope","brain","beaker","skull","bone","construction","construction-cone","pie-chart","pie-chart2","graph","chart-growth","chart-bars","chart-settings","cake","gift","balloon","rank","rank2","rank3","crown","lotus","diamond","diamond2","diamond3","diamond4","linearicons","teacup","teapot","glass","bottle2","glass-cocktail","glass2","dinner","dinner2","chef","scale3","egg","egg2","eggs","platter","steak","hamburger","hotdog","pizza","sausage","chicken","fish","carrot","cheese","bread","ice-cream","ice-cream2","candy","lollipop","coffee-bean","coffee-cup","cherry","grapes","citrus","apple","leaf","landscape","pine-tree","tree","cactus","paw","footprint","speed-slow","speed-medium","speed-fast","rocket","hammer2","balance","briefcase","luggage-weight","dolly","plane","plane-crossed","helicopter","traffic-lights","siren","road","engine","oil-pressure","coolant-temperature","car-battery","gas","gallon","transmission","car","car-wash","car-wash2","bus","bus2","car2","parking","car-lock","taxi","car-siren","car-wash3","car-wash4","ambulance","truck","trailer","scale-truck","train","ship","ship2","anchor","boat","bicycle","bicycle2","dumbbell","bench-press","swim","football","baseball-bat","baseball","tennis","tennis2","ping-pong","hockey","8ball","bowling","bowling-pins","golf","golf2","archery","slingshot","soccer","basketball","cube","3d-rotate","puzzle","glasses2","glasses3","accessibility","wheelchair","wall","fence","wall3","icons","resize-handle","icons2","select","select2","site-map","earth","earth-lock","network","network-lock","planet","happy","smile","grin","tongue","sad","wink","dream","shocked","shocked2","tongue2","neutral","happy-grin","cool","mad","grin-evil","evil","wow","annoyed","wondering","confused","zipped","grumpy","mustache","tombstone-hipster","tombstone","ghost","ghost-hipster","halloween","christmas","easter-egg","mustache2","mustache-glasses","pipe","alarm2","alarm-add","alarm-snooze","alarm-ringing","bullhorn","hearing","volume-high","volume-medium","volume-low","volume","mute","lan","lan2","wifi","wifi-lock","wifi-blocked","wifi-mid","wifi-low","wifi-low2","wifi-alert","wifi-alert-mid","wifi-alert-low","wifi-alert-low2","stream","stream-check","stream-error","stream-alert","communication","communication-crossed","broadcast2","antenna","satellite","satellite2","mic","mic-mute","mic2","spotlights","hourglass","loading2","loading3","loading4","refresh","refresh2","undo","redo","jump2","undo2","redo2","sync","repeat-one2","sync-crossed","sync2","repeat-one3","sync-crossed2","return","return2","refund","history","history2","self-timer","clock","clock2","clock3","watch","alarm3","alarm-add2","alarm-remove","alarm-check","alarm-error","timer","timer-crossed","timer2","timer-crossed2","download","upload","download2","upload2","enter-up","enter-down","enter-left","enter-right","exit-up","exit-down","exit-left","exit-right","enter-up2","enter-down2","enter-vertical","enter-left2","enter-right2","enter-horizontal","exit-up2","exit-down2","exit-left2","exit-right2","cli","bug","code","file-code","file-image","file-zip","file-audio","file-video","file-preview","file-charts","file-stats","file-spreadsheet","link","unlink","link2","unlink2","thumbs-up","thumbs-down","thumbs-up2","thumbs-down2","thumbs-up3","thumbs-down3","share2","share3","share4","magnifier","file-search","find-replace","zoom-in","zoom-out","loupe","loupe-zoom-in","loupe-zoom-out","cross","menu","list","list2","list3","menu2","list4","menu3","exclamation","question","check","cross2","plus","minus","percent","chevron-up","chevron-down","chevron-left","chevron-right","chevrons-expand-vertical","chevrons-expand-horizontal","chevrons-contract-vertical","chevrons-contract-horizontal","arrow-up","arrow-down","arrow-left","arrow-right","arrow-up-right","arrows-merge","arrows-split","arrow-divert","arrow-return","expand","contract","expand2","contract2","move","tab","arrow-wave","expand3","expand4","contract3","notification","warning","notification-circle","question-circle","menu-circle","checkmark-circle","cross-circle","plus-circle","circle-minus","percent-circle","arrow-up-circle","arrow-down-circle","arrow-left-circle","arrow-right-circle","chevron-up-circle","chevron-down-circle","chevron-left-circle","chevron-right-circle","backward-circle","first-circle","previous-circle","stop-circle","play-circle","pause-circle","next-circle","last-circle","forward-circle","eject-circle","crop","frame-expand","frame-contract","focus","transform","grid3","grid-crossed","layers","layers-crossed","toggle4","rulers","ruler","funnel","flip-horizontal","flip-vertical","flip-horizontal2","flip-vertical2","angle","angle2","subtract2","combine","intersect","exclude","align-center-vertical","align-right","align-bottom","align-left","align-center-horizontal","align-top","square","plus-square","minus-square","percent-square","arrow-up-square","arrow-down-square","arrow-left-square","arrow-right-square","chevron-up-square","chevron-down-square","chevron-left-square","chevron-right-square","check-square","cross-square","menu-square","prohibited","circle","radio-button","ligature","text-format","text-format-remove","text-size","bold","italic","underline","strikethrough","highlight","text-align-left","text-align-center","text-align-right","text-align-justify","line-spacing","indent-increase","indent-decrease","text-wrap","pilcrow","direction-ltr","direction-rtl","page-break","page-break2","sort-alpha-asc","sort-alpha-desc","sort-numeric-asc","sort-numeric-desc","sort-amount-asc","sort-amount-desc","sort-time-asc","sort-time-desc","sigma","pencil-line","hand","pointer-up","pointer-right","pointer-down","pointer-left","finger-tap","fingers-tap","reminder","fingers-crossed","fingers-victory","gesture-zoom","gesture-pinch","fingers-scroll-horizontal","fingers-scroll-vertical","fingers-scroll-left","fingers-scroll-right","hand17","pointer-up2","pointer-right2","pointer-down2","pointer-left2","finger-tap2","fingers-tap2","reminder2","gesture-zoom2","gesture-pinch2","fingers-scroll-horizontal2","fingers-scroll-vertical2","fingers-scroll-left2","fingers-scroll-right2","fingers-scroll-vertical3","border-style","border-all","border-outer","border-inner","border-top","border-horizontal","border-bottom","border-left","border-vertical","border-right","border-none","ellipsis"];

  iconSelect(props) {

    const handleChange = (icon) => {
      return () => {
        props.input.onChange(icon);
      };
    };

    return (
      <div className={"icon-select-component"}>
        <div className="selected-icon" onClick={this.openIconSelect}>

          <LinearIcon type={props.input.value || 'cube'}/>

          {props.visible && (
            <div className="icons-list">
              {this.icons.map((icon, index) => {
                return (
                  <div key={index} className={"icon-container"} onClick={handleChange(icon)}>
                    <LinearIcon type={icon}/>
                  </div>
                );
              })}
            </div>
          )}

        </div>
      </div>
    );
  }

  render() {

    return (
      <Field name={this.props.name} component={this.iconSelect} visible={this.state.selectOpened}/>
    );
  }
}
