import React from 'react';
import {Modal} from 'components';
import {Button, DatePicker} from 'antd';

class SpecificTimeSelect extends React.Component {

  static propTypes = {
    visible: React.PropTypes.bool,
    onChange: React.PropTypes.func,
    onCancel: React.PropTypes.func,
  };

  handleCancelClick() {
    this.props.onCancel();
  }

  handleOkClick() {
    this.props.onCancel();
  }

  render() {
    return (
      <Modal title="Select Time Range"
             visible={this.props.visible}
             onCancel={this.handleCancelClick.bind(this)}
             footer={[
               <Button key="cancel" type="default" size="small"
                       onClick={this.handleCancelClick.bind(this)}>Cancel</Button>,
               <Button key="save" type="primary" size="small"
                       onClick={this.handleOkClick.bind(this)}>
                 Select Range
               </Button>,
             ]}>
        <DatePicker.RangePicker format="YYYY-MM-DD HH:mm:ss" showTime={true} style={{width: '100%'}}/>
      </Modal>
    );
  }

}

export default SpecificTimeSelect;
