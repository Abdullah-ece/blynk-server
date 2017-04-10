import React from 'react';

import {SketchPicker} from 'react-color';

import classNames from 'classnames';

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

    const pickerClass = classNames({
      'branding-color-picker-box-color-code': true,
      'placeholder': !this.state.color
    });

    return (
      <div className="branding-color-picker">
        { this.state.displayColorPicker &&
        <div className="branding-color-picker-color-preview-overlay" onClick={this.hideColorPicker.bind(this)}/> }
        <div className="branding-color-picker-name">
          { this.props.title }
        </div>
        <div className="branding-color-picker-box">
          <div className={pickerClass}>
            { this.state.color || 'Choose' }
          </div>
          <div className="branding-color-picker-color-preview">
            { this.state.displayColorPicker &&
            <SketchPicker color={this.state.color} onChange={this.handleColorChange.bind(this)}/>}
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
