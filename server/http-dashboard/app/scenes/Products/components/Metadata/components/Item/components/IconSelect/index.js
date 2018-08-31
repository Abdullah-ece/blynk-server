import React, {Component} from 'react';
import {Input, Item} from 'components/UI';
import {Icon} from 'antd';
import PropTypes from 'prop-types';
import {change} from 'redux-form';
import {FORMS} from "services/Products/index";
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import './styles.less';


@connect(() => ({}), (dispatch) => ({
  changeForm: bindActionCreators(change, dispatch)
}))
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
  }

  componentDidMount() {
    this.handleIconChange("key");
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

  icons = [
    "bars",
    "book",
    "calendar",
    "cloud",
    "cloud-download",
    "code",
  ];

  handleIconChange(icon) {
    this.props.changeForm(FORMS.PRODUCTS_PRODUCT_MANAGE,this.props.name, icon);
    this.setState({
      selectedIcon: icon,
    });
  }

  render() {

    const selectedIcon = this.state && this.state.selectedIcon || "default";

    return (
      <div className={"icon-select-component"}>
        <Item>
          <Input type={"hidden"} name={this.props.name} placeholder="icon" />
        </Item>
        <div className="selected-icon" onClick={this.openIconSelect}>
          <Icon type={selectedIcon}/>
          {this.state.selectOpened && (
            <div className="icons-list">
              {this.icons.map((icon, index) => {
                return (
                  <div key={index} className={"icon-container"} onClick={()=>{this.handleIconChange(icon)}}>
                    <Icon type={icon}/>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>
    );
  }
}
