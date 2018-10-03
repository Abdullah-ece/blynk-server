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

  icons = ["e600","e600","e601","e601","e602","e602","e603","e603","e604","e604","e605","e605","e606","e606","e607","e607","e608","e608","e609","e609","e60a","e60a","e60b","e60b","e60c","e60c","e60d","e60d","e60e","e60e","e60f","e60f","e610","e610","e611","e611","e612","e612","e613","e613","e614","e614","e615","e615","e616","e616","e617","e617","e618","e619","e61a","e61a","e61b","e61b","e61c","e61c","e61d","e61d","e61e","e61e","e61f","e61f","e620","e620","e621","e621","e622","e622","e623","e623","e624","e624","e625","e625","e626","e626","e627","e627","e628","e628","e629","e62a","e62b","e62b","e62c","e62c","e62d","e62d","e62e","e62e","e62f","e62f","e630","e630","e631","e631","e632","e632","e633","e633","e634","e634","e635","e635","e636","e636","e637","e637","e638","e638","e639","e639","e63a","e63a","e63b","e63b","e63c","e63c","e63d","e63d","e63e","e63e","e63f","e63f","e640","e640","e641","e641","e642","e642","e643","e643","e644","e644","e645","e646","e646","e647","e647","e648","e648","e649","e649","e64a","e64a","e64b","e64b","e64c","e64c","e64d","e64d","e64e","e64e","e64f","e64f","e650","e650","e651","e651","e652","e652","e653","e653","e654","e654","e655","e655","e656","e656","e657","e657","e658","e658","e659","e659","e65a","e65a","e65b","e65b","e65c","e65c","e65d","e65d","e65e","e65e","e65f","e65f","e660","e660","e661","e661","e662","e662","e663","e663","e664","e664","e665","e665","e666","e666","e667","e667","e668","e668","e669","e669","e66a","e66a","e66b","e66b","e66c","e66c","e66d","e66d","e66e","e66e","e66f","e66f","e670","e670","e671","e671","e672","e672","e673","e673","e674","e674","e675","e675","e676","e676","e677","e677","e678","e678","e679","e679","e67a","e67a","e67b","e67b","e67c","e67c","e67d","e67d","e67e","e67e","e67f","e680","e680","e681","e681","e682","e682","e683","e683","e684","e684","e685","e685","e686","e686","e687","e687","e688","e688","e689","e689","e68a","e68a","e68b","e68b","e68c","e68c","e68d","e68d","e68e","e68e","e68f","e68f","e690","e690","e691","e691","e692","e692","e693","e693","e694","e694","e695","e695","e696","e696","e697","e697","e698","e698","e699","e699","e69a","e69a","e69b","e69b","e69c","e69c","e69d","e69d","e69e","e69e","e69f","e69f","e6a0","e6a0","e6a1","e6a1","e6a2","e6a2","e6a3","e6a3","e6a4","e6a4","e6a5","e6a5","e6a6","e6a6","e6a7","e6a7","e6a8","e6a8","e6a9","e6a9","e6aa","e6aa","e6ab","e6ab","e6ac","e6ac","e6ad","e6ad","e6ae","e6ae","e6af","e6af","e6b0","e6b0","e6b1","e6b1","e6b2","e6b2","e6b3","e6b3","e6b4","e6b4","e6b5","e6b5","e6b6","e6b6","e6b7","e6b7","e6b8","e6b8","e6b9","e6b9","e6ba","e6ba","e6bb","e6bb","e6bc","e6bc","e6bd","e6bd","e6be","e6be","e6bf","e6bf","e6c0","e6c0","e6c1","e6c1","e6c2","e6c2","e6c3","e6c3","e6c4","e6c4","e6c5","e6c5","e6c6","e6c6","e6c7","e6c7","e6c8","e6c8","e6c9","e6c9","e6ca","e6ca","e6cb","e6cb","e6cc","e6cc","e6cd","e6cd","e6ce","e6ce","e6cf","e6cf","e6d0","e6d0","e6d1","e6d1","e6d2","e6d2","e6d3","e6d3","e6d4","e6d4","e6d5","e6d5","e6d6","e6d6","e6d7","e6d7","e6d8","e6d8","e6d9","e6d9","e6da","e6da","e6db","e6db","e6dc","e6dc","e6dd","e6dd","e6de","e6de","e6df","e6df","e6e0","e6e0","e6e1","e6e1","e6e2","e6e2","e6e3","e6e3","e6e4","e6e4","e6e5","e6e5","e6e6","e6e6","e6e7","e6e8","e6e9","e6e9","e6ea","e6ea","e6eb","e6eb","e6ec","e6ec","e6ed","e6ed","e6ee","e6ee","e6ef","e6ef","e6f0","e6f0","e6f1","e6f1","e6f2","e6f2","e6f3","e6f3","e6f4","e6f4","e6f5","e6f5","e6f6","e6f6","e6f7","e6f7","e6f8","e6f8","e6f9","e6f9","e6fa","e6fa","e6fb","e6fb","e6fc","e6fc","e6fd","e6fd","e6fe","e6fe","e6ff","e6ff","e700","e700","e701","e701","e702","e702","e703","e703","e704","e704","e705","e706","e706","e707","e707","e708","e708","e709","e70a","e70b","e70c","e70d","e70e","e70e","e70f","e70f","e710","e710","e711","e711","e712","e712","e713","e713","e714","e714","e715","e715","e716","e716","e717","e718","e718","e719","e719","e71a","e71a","e71b","e71b","e71c","e71c","e71d","e71d","e71e","e71e","e71f","e71f","e720","e720","e721","e721","e722","e722","e723","e723","e724","e724","e725","e725","e726","e726","e727","e727","e728","e728","e729","e729","e72a","e72a","e72b","e72b","e72c","e72c","e72d","e72d","e72e","e72e","e72f","e72f","e730","e730","e731","e731","e732","e732","e733","e733","e734","e735","e735","e736","e737","e737","e738","e738","e739","e739","e73a","e73a","e73b","e73b","e73c","e73c","e73d","e73d","e73e","e73e","e73f","e73f","e740","e740","e741","e741","e742","e742","e743","e743","e744","e744","e745","e745","e746","e746","e747","e747","e748","e748","e749","e749","e74a","e74a","e74b","e74b","e74c","e74c","e74d","e74d","e74e","e74e","e74f","e74f","e750","e750","e751","e751","e752","e752","e753","e753","e754","e754","e755","e755","e756","e756","e757","e757","e758","e758","e759","e759","e75a","e75a","e75b","e75b","e75c","e75c","e75d","e75d","e75e","e75e","e75f","e75f","e760","e760","e761","e761","e762","e762","e763","e763","e764","e764","e765","e765","e766","e766","e767","e767","e768","e768","e769","e769","e76a","e76a","e76b","e76b","e76c","e76c","e76d","e76d","e76e","e76e","e76f","e76f","e770","e770","e771","e771","e772","e772","e773","e773","e774","e774","e775","e775","e776","e776","e777","e777","e778","e778","e779","e779","e77a","e77a","e77b","e77b","e77c","e77c","e77d","e77d","e77e","e77e","e77f","e77f","e780","e780","e781","e781","e782","e782","e783","e783","e784","e784","e785","e785","e786","e786","e787","e787","e788","e788","e789","e789","e78a","e78a","e78b","e78b","e78c","e78c","e78d","e78d","e78e","e78e","e78f","e78f","e790","e790","e791","e791","e792","e792","e793","e793","e794","e794","e795","e795","e796","e797","e798","e798","e799","e799","e79a","e79a","e79b","e79b","e79c","e79c","e79d","e79d","e79e","e79e","e79f","e79f","e7a0","e7a0","e7a1","e7a1","e7a2","e7a2","e7a3","e7a4","e7a4","e7a5","e7a5","e7a6","e7a6","e7a7","e7a7","e7a8","e7a8","e7a9","e7a9","e7aa","e7aa","e7ab","e7ab","e7ac","e7ac","e7ad","e7ad","e7ae","e7ae","e7af","e7af","e7b0","e7b0","e7b1","e7b1","e7b2","e7b2","e7b3","e7b3","e7b4","e7b4","e7b5","e7b5","e7b6","e7b6","e7b7","e7b8","e7b8","e7b9","e7b9","e7ba","e7ba","e7bb","e7bb","e7bc","e7bc","e7bd","e7bd","e7be","e7be","e7bf","e7bf","e7c0","e7c0","e7c1","e7c1","e7c2","e7c2","e7c3","e7c3","e7c4","e7c4","e7c5","e7c5","e7c6","e7c6","e7c7","e7c7","e7c8","e7c8","e7c9","e7c9","e7ca","e7ca","e7cb","e7cb","e7cc","e7cc","e7cd","e7cd","e7ce","e7ce","e7cf","e7cf","e7d0","e7d0","e7d1","e7d1","e7d2","e7d2","e7d3","e7d3","e7d4","e7d4","e7d5","e7d5","e7d6","e7d6","e7d7","e7d7","e7d8","e7d8","e7d9","e7d9","e7da","e7da","e7db","e7db","e7dc","e7dc","e7dd","e7dd","e7de","e7de","e7df","e7df","e7e0","e7e0","e7e1","e7e1","e7e2","e7e2","e7e3","e7e3","e7e4","e7e4","e7e5","e7e6","e7e7","e7e7","e7e8","e7e8","e7e9","e7e9","e7ea","e7ea","e7eb","e7eb","e7ec","e7ec","e7ed","e7ed","e7ee","e7ee","e7ef","e7ef","e7f0","e7f0","e7f1","e7f1","e7f2","e7f2","e7f3","e7f3","e7f4","e7f4","e7f5","e7f5","e7f6","e7f6","e7f7","e7f7","e7f8","e7f8","e7f9","e7f9","e7fa","e7fa","e7fb","e7fb","e7fc","e7fc","e7fd","e7fd","e7fe","e7fe","e7ff","e7ff","e800","e800","e801","e801","e802","e802","e803","e803","e804","e804","e805","e805","e806","e806","e807","e807","e808","e808","e809","e809","e80a","e80a","e80a","e80b","e80b","e80c","e80c","e80d","e80d","e80e","e80e","e80f","e80f","e810","e810","e811","e811","e812","e812","e813","e813","e814","e814","e815","e815","e816","e816","e817","e817","e818","e818","e819","e819","e81a","e81a","e81b","e81b","e81c","e81c","e81d","e81d","e81e","e81e","e81f","e81f","e820","e820","e821","e821","e822","e822","e823","e823","e824","e824","e825","e825","e826","e826","e827","e827","e828","e828","e829","e829","e82a","e82a","e82b","e82b","e82c","e82c","e82d","e82d","e82e","e82e","e82f","e82f","e830","e830","e831","e831","e832","e832","e833","e833","e834","e834","e835","e835","e836","e836","e837","e837","e838","e838","e839","e839","e83a","e83a","e83b","e83b","e83c","e83c","e83d","e83d","e83e","e83e","e83f","e83f","e840","e840","e841","e841","e842","e842","e843","e843","e844","e845","e845","e846","e846","e847","e847","e848","e848","e849","e849","e84a","e84a","e84b","e84c","e84d","e84d","e84e","e84e","e84f","e84f","e850","e851","e852","e852","e853","e853","e854","e855","e856","e856","e857","e857","e858","e858","e859","e859","e85a","e85a","e85b","e85b","e85c","e85c","e85d","e85d","e85e","e85e","e85f","e85f","e860","e860","e861","e861","e862","e862","e863","e863","e864","e864","e865","e865","e866","e866","e867","e867","e868","e868","e869","e869","e86a","e86a","e86b","e86b","e86c","e86c","e86d","e86d","e86e","e86e","e86f","e86f","e870","e870","e871","e871","e872","e872","e873","e873","e874","e874","e875","e876","e876","e877","e877","e878","e878","e879","e87a","e87a","e87b","e87b","e87c","e87c","e87d","e87d","e87e","e87e","e87f","e880","e880","e881","e881","e882","e882","e883","e883","e884","e884","e885","e885","e886","e886","e887","e887","e888","e888","e889","e889","e88a","e88a","e88b","e88b","e88c","e88c","e88d","e88d","e88e","e88e","e88f","e88f","e890","e890","e891","e891","e892","e892","e893","e893","e894","e894","e895","e895","e896","e896","e897","e897","e898","e898","e899","e899","e89a","e89a","e89b","e89b","e89c","e89c","e89d","e89d","e89e","e89e","e89f","e89f","e8a0","e8a0","e8a1","e8a1","e8a2","e8a2","e8a3","e8a3","e8a4","e8a4","e8a5","e8a5","e8a6","e8a6","e8a7","e8a7","e8a8","e8a8","e8a9","e8a9","e8aa","e8aa","e8ab","e8ab","e8ac","e8ac","e8ad","e8ad","e8ae","e8ae","e8af","e8af","e8b0","e8b0","e8b1","e8b1","e8b2","e8b2","e8b3","e8b3","e8b4","e8b4","e8b5","e8b5","e8b6","e8b6","e8b7","e8b7","e8b8","e8b8","e8b9","e8b9","e8ba","e8ba","e8bb","e8bb","e8bc","e8bc","e8bd","e8bd","e8be","e8be","e8bf","e8bf","e8c0","e8c0","e8c1","e8c1","e8c2","e8c2","e8c3","e8c3","e8c4","e8c4","e8c5","e8c5","e8c6","e8c6","e8c7","e8c7","e8c8","e8c8","e8c9","e8c9","e8ca","e8ca","e8cb","e8cb","e8cc","e8cc","e8cd","e8cd","e8ce","e8ce","e8cf","e8cf","e8d0","e8d0","e8d1","e8d1","e8d2","e8d2","e8d3","e8d3","e8d4","e8d4","e8d5","e8d5","e8d6","e8d6","e8d7","e8d7","e8d8","e8d8","e8d9","e8d9","e8da","e8da","e8db","e8dc","e8dd","e8dd","e8de","e8df","e8e0","e8e0","e8e1","e8e1","e8e2","e8e2","e8e3","e8e3","e8e4","e8e4","e8e5","e8e6","e8e6","e8e7","e8e7","e8e8","e8e8","e8e9","e8e9","e8ea","e8ea","e8eb","e8eb","e8ec","e8ec","e8ed","e8ed","e8ee","e8ee","e8ef","e8ef","e8f0","e8f0","e8f1","e8f1","e8f2","e8f2","e8f3","e8f3","e8f4","e8f4","e8f5","e8f5","e8f6","e8f6","e8f7","e8f7","e8f8","e8f8","e8f9","e8f9","e8fa","e8fa","e8fb","e8fb","e8fc","e8fc","e8fd","e8fd","e8fe","e8fe","e8ff","e8ff","e900","e900","e901","e902","e902","e903","e903","e904","e904","e905","e905","e906","e906","e907","e907","e908","e908","e909","e909","e90a","e90a","e90b","e90b","e90c","e90c","e90d","e90d","e90e","e90e","e90f","e90f","e910","e910","e911","e911","e912","e912","e913","e913","e914","e914","e915","e915","e916","e916","e917","e917","e918","e918","e919","e919","e91a","e91a","e91b","e91b","e91c","e91c","e91d","e91d","e91e","e91e","e91f","e91f","e920","e920","e921","e921","e922","e922","e923","e923","e924","e924","e925","e925","e926","e926","e927","e927","e928","e928","e929","e929","e92a","e92a","e92b","e92b","e92c","e92c","e92d","e92d","e92e","e92e","e92f","e92f","e930","e930","e931","e931","e932","e932","e933","e933","e934","e934","e935","e935","e936","e936","e937","e937","e938","e938","e939","e939","e93a","e93a","e93b","e93b","e93c","e93c","e93d","e93d","e93e","e93e","e93f","e93f","e940","e940","e941","e941","e942","e942","e943","e943","e944","e944","e945","e945","e946","e946","e947","e947","e948","e948","e949","e949","e94a","e94a","e94b","e94b","e94c","e94c","e94d","e94d","e94e","e94e","e94f","e94f","e950","e951","e951","e952","e952","e953","e953","e954","e954","e955","e955","e956","e956","e957","e957","e958","e958","e959","e959","e95a","e95a","e95b","e95b","e95c","e95c","e95d","e95d","e95e","e95e","e95f","e95f","e960","e960","e961","e961","e962","e962","e963","e963","e964","e964","e965","e965","e966","e966","e967","e967","e968","e968","e969","e969","e96a","e96a","e96b","e96b","e96c","e96c","e96d","e96d","e96e","e96e","e96f","e96f","e970","e970","e971","e971","e972","e972","e973","e973","e974","e974","e975","e975","e976","e976","e977","e977","e978","e978","e979","e979","e97a","e97a","e97b","e97b","e97c","e97c","e97d","e97d","e97e","e97e","e97f","e97f","e980","e980","e981","e981","e982","e982","e983","e983","e984","e984","e985","e985","e986","e986","e987","e987","e988","e988","e989","e989","e98a","e98a","e98b","e98b","e98c","e98c","e98d","e98d","e98e","e98e","e98f","e98f","e990","e990","e991","e991","e992","e992","e993","e993","e994","e994","e995","e995","e996","e996","e997","e997","e998","e998","e999","e999","e99a","e99a","e99b","e99b","e99c","e99c","e99d","e99d","e99e","e99f","e99f","e9a0","e9a0","e9a1","e9a1","e9a2","e9a2","e9a3","e9a3","e9a4","e9a4","e9a5","e9a5","e9a6","e9a6","e9a7","e9a7","e9a8","e9a8","e9a9","e9a9","e9aa","e9aa","e9ab","e9ab","e9ac","e9ac","e9ad","e9ad","e9ae","e9ae","e9af","e9af","e9b0","e9b0","e9b1","e9b1","e9b2","e9b2","e9b3","e9b4","e9b5","e9b5","e9b6","e9b6","e9b7","e9b7","e9b8","e9b8","e9b9","e9b9","e9ba","e9ba","e9bb","e9bb","e9bc","e9bc","e9bd","e9bd","e9be","e9be","e9bf","e9bf","e9c0","e9c0","e9c1","e9c1","e9c2","e9c2","e9c3","e9c3","e9c4","e9c4","e9c5","e9c5","e9c6","e9c6","e9c7","e9c7","e9c8","e9c8","e9c9","e9c9","e9ca","e9ca","e9cb","e9cb","e9cc","e9cc","e9cd","e9cd","e9ce","e9ce","e9cf","e9cf","e9d0","e9d0","e9d1","e9d1","e9d2","e9d2","e9d3","e9d3","e9d4","e9d4","e9d5","e9d5","e9d6","e9d6","e9d7","e9d7","e9d8","e9d8","e9d9","e9d9","e9da","e9da","e9db","e9db","e9dc","e9dc","e9dd","e9dd","e9de","e9de","e9df","e9df","e9e0","e9e0","e9e1","e9e1","e9e2","e9e2","e9e3","e9e3","e9e4","e9e4","e9e5","e9e5","e9e6","e9e6","e9e7","e9e7","e9e8","e9e8","e9e9","e9e9"];

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
