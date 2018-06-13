import React from 'react';

import {TwitterPicker} from 'react-color';

import PropTypes from 'prop-types';

import './styles.less';

class BrandingColorPicker extends React.Component {

  static propTypes = {
    style: PropTypes.object,
    title: PropTypes.string,
    color: PropTypes.any,
    onChange: PropTypes.func,
    colors: PropTypes.array,
    width: PropTypes.string,
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

    let {style} = this.props;

    if(!style)
      style = {};

    return (
      <div className="branding-color-picker" style={style}>
        { this.state.displayColorPicker &&
        <div className="branding-color-picker-color-preview-overlay" onClick={this.hideColorPicker}/> }
        <div className="color-picker">
          <div className="branding-color-picker-color-preview">
            { this.state.displayColorPicker &&
            <TwitterPicker triangle="hide" width={this.props.width} color={this.props.color} onChange={this.handleColorChange} colors={this.props.colors || []}/>}
            { !this.props.color &&
            <div className="branding-color-picker-color-preview-choose" onClick={this.displayColorPicker}/>
            }
            { this.props.color &&
            <div className="branding-color-picker-color-preview-specific" onClick={this.displayColorPicker}
                 style={{background: '#' + this.props.color}}/> }
          </div>
        </div>
      </div>
    );
  }
}

export default BrandingColorPicker;
