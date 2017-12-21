import React from 'react';

import {TwitterPicker} from 'react-color';

import './styles.less';

class BrandingColorPicker extends React.Component {

  static propTypes = {
    title: React.PropTypes.string,
    color: React.PropTypes.any,
    backgroundColor: React.PropTypes.any,
    onChange: React.PropTypes.func,
    colors: React.PropTypes.array,
  };

  constructor(props) {
    super(props);

    this.state = {
      displayColorPicker: false
    };
    this.displayColorPicker = this.displayColorPicker.bind(this);
    this.hideColorPicker    = this.hideColorPicker.bind(this);
    this.handleColorChange = this.handleColorChange.bind(this);
  }

  displayColorPicker() {
    this.setState({
      displayColorPicker: true
    });
  }

  hideColorPicker() {
    this.setState({
      displayColorPicker: false
    });
  }

  handleColorChange(color) {
    if (this.props.onChange) this.props.onChange(color.hex.replace('#', ''));
  }

  render() {
    return (
      <div className="branding-text-color-picker">
        { this.state.displayColorPicker &&
        <div className="branding-text-color-picker-color-preview-overlay" onClick={this.hideColorPicker}/> }
        <div className="color-picker">
          <div className="branding-text-color-picker-color-preview">
            { this.state.displayColorPicker &&
            <TwitterPicker triangle="hide" color={this.props.color} onChange={this.handleColorChange} colors={this.props.colors || []}/>}
            { !this.props.color &&
            <div className="branding-text-color-picker-color-preview-choose" onClick={this.displayColorPicker}/>
            }
            { this.props.color &&
            <div className="branding-text-color-picker-color-preview-specific" onClick={this.displayColorPicker}
                 style={{background: '#' + this.props.backgroundColor}}/> }
                 <span onClick={this.displayColorPicker} style={{color: '#' + this.props.color}} className="branding-text-color-picker-color-preview-specific-label">T</span>
          </div>
        </div>
      </div>
    );
  }
}

export default BrandingColorPicker;
