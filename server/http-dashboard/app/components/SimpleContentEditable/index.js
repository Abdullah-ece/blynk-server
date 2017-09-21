import React from 'react';
import ContentEditable from './components/ContentEditable';
import classnames from 'classnames';
import PropTypes from 'prop-types';
import './styles.less';

class SimpleContentEditable extends React.Component {

  static propTypes = {
    className: PropTypes.string,
    value: PropTypes.string,

    onChange: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleBlur = this.handleBlur.bind(this);
    this.handleChange = this.handleChange.bind(this);
  }

  shouldComponentUpdate() {
    return false;
  }

  state = {
    isFocused: false
  };

  handleChange(event) {
    if (this.props.onChange)
      this.props.onChange(event.target.value.replace(/&nbsp;/g, ' '));
  }

  handleBlur(event) {
    const value = event.target.value.replace(/&nbsp;/g, ' ').replace(/ /g, '');
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
        <ContentEditable html={this.props.value} onChange={this.handleChange} onBlur={this.handleBlur}/>
      </div>
    );
  }

}

export default SimpleContentEditable;
