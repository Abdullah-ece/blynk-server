import React from 'react';
import {Checkbox as NativeCheckbox, Popconfirm} from 'antd';

class Checkbox extends React.Component {

  static propTypes = {
    input: React.PropTypes.object,
    placeholder: React.PropTypes.string,
    popconfirm: React.PropTypes.shape({
      onCheck: React.PropTypes.bool,
      onUncheck: React.PropTypes.bool,
      message: React.PropTypes.any,
      onCancel: React.PropTypes.func,
      onConfirm: React.PropTypes.func,
    }),
  };

  constructor(props) {
    super(props);

    this.toggle = this.toggle.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.handleConfirm = this.handleConfirm.bind(this);
  }

  shouldComponentUpdate(nextProps) {
    return this.props.input.value !== nextProps.input.value || this.props.placeholder !== nextProps.placeholder || this.props.popconfirm !== nextProps.popconfirm;
  }

  handleConfirm() {
    this.props.input.onChange(!this.props.input.value);
    if (this.props.popconfirm.onConfirm) {
      this.props.popconfirm.onConfirm();
    }
  }

  handleCancel() {
    if (this.props.popconfirm.onCancel) {
      this.props.popconfirm.onCancel();
    }
  }

  toggle(event) {
    if (this.props.popconfirm.onCheck && !!event.target.checked) {
      return true;
    }
    if (this.props.popconfirm.onUncheck && !event.target.checked) {
      return true;
    }
    this.props.input.onChange(event.target.checked);
  }

  render() {
    if (this.props.popconfirm && this.props.popconfirm.message && (
        (this.props.popconfirm.onCheck && !this.props.input.value) ||
        (this.props.popconfirm.onUncheck && this.props.input.value))) {

      return (
        <Popconfirm title={this.props.popconfirm.message}
                    okText="Yes"
                    cancelText="No"
                    onConfirm={this.handleConfirm}
                    onCancel={this.handleCancel}
                    overlayClassName="danger">
          <NativeCheckbox onChange={this.toggle}
                          checked={!!this.props.input.value}>{this.props.placeholder}</NativeCheckbox>
        </Popconfirm>
      );
    }

    return (
      <NativeCheckbox onChange={this.props.input.onChange}
                      checked={!!this.props.input.value}>{this.props.placeholder}</NativeCheckbox>
    );
  }

}

export default Checkbox;
