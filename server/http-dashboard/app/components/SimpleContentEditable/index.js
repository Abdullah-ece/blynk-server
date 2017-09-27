import React from 'react';
import AutosizeInput from 'react-input-autosize';
import classnames from 'classnames';
import PropTypes from 'prop-types';
import './styles.less';

class SimpleContentEditable extends React.Component {

  static propTypes = {
    className: PropTypes.string,
    value: PropTypes.string,
    maxLength: PropTypes.number,

    onChange: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleBlur = this.handleBlur.bind(this);
    this.handleChange = this.handleChange.bind(this);
  }

  state = {
    isFocused: false
  };

  handleChange(event) {
    if (this.props.onChange) {
      if (this.props.maxLength === undefined || event.target.value.length <= this.props.maxLength) {
        this.props.onChange(event.target.value);
      }
    }
  }

  handleBlur(event) {
    const value = event.target.value;
    if (!value.trim()) {
      this.props.onChange('No Name');
    }
  }

  render() {

    const className = classnames({
      'simple-content-editable': true,
      [this.props.className]: !!this.props.className,
    });

    return (
      <div className={className}>
        <AutosizeInput type="text" value={this.props.value} onChange={this.handleChange} onBlur={this.handleBlur}/>
      </div>
    );
  }

}

export default SimpleContentEditable;
