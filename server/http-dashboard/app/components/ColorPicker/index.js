import React from 'react';

import {TwitterPicker} from 'react-color';

import './styles.less';

class BrandingColorPicker extends React.Component {

  static propTypes = {
    title: React.PropTypes.string,
    color: React.PropTypes.any,
    onChange: React.PropTypes.func
  };

  constructor(props) {
    super(props);

    this.state = {
      color: props.color || '',
      displayColorPicker: false
    };
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
    if (this.props.onChange) this.props.onChange(this.state.color);
  }

  handleColorChange(color) {
    this.setState({
      color: color.hex.replace('#', '')
    });
  }

  render() {

    return (
      <div className="branding-color-picker">
        { this.state.displayColorPicker &&
        <div className="branding-color-picker-color-preview-overlay" onClick={this.hideColorPicker.bind(this)}/> }
        <div className="color-picker">
          <div className="branding-color-picker-color-preview">
            { this.state.displayColorPicker &&
            <TwitterPicker triangle="hide" color={this.state.color} onChange={this.handleColorChange.bind(this)}/>}
            { !this.state.color &&
            <div className="branding-color-picker-color-preview-choose" onClick={this.displayColorPicker.bind(this)}/>
            }
            { this.state.color &&
            <div className="branding-color-picker-color-preview-specific" onClick={this.displayColorPicker.bind(this)}
                 style={{background: '#' + this.state.color}}/> }
          </div>
        </div>
      </div>
    );
  }
}

export default BrandingColorPicker;
