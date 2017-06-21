import React            from 'react';
import {Input, Item}    from 'components/UI';
import {Modal}          from 'components';
import {Button}         from 'antd';
import classnames       from 'classnames';
import moment           from 'moment';
import {EVENT_TYPES}    from 'services/Products';
import './styles.less';

class MarkAsResolvedModal extends React.Component {

  static propTypes = {
    onCancel: React.PropTypes.func,
    onMarkAsResolved: React.PropTypes.func,
    event: React.PropTypes.object,
    deviceId: React.PropTypes.number,
    isModalVisible: React.PropTypes.bool,
  };

  state = {
    loading: false
  };

  handleOk() {

  }

  handleCancel() {
    this.props.onCancel();
  }

  render() {

    if (!this.props.isModalVisible)
      return null;

    const classNames = classnames({
      'event-resolve-modal': true,
      'event-resolve-modal--critical': this.props.event.get('eventType') === EVENT_TYPES.CRITICAL,
      'event-resolve-modal--warning': this.props.event.get('eventType') === EVENT_TYPES.WARNING,
    });

    const time = moment(this.props.event.get('ts')).calendar(null, {
      sameDay: '[Today], hh:mm A',
      lastDay: '[Yesterday], hh:mm A',
      lastWeek: 'dddd, hh:mm A',
      sameElse: 'MMM D, YYYY hh:mm A'
    });

    return (
      <Modal visible={this.props.isModalVisible}
             confirmLoading={this.state.loading}
             onCancel={this.handleCancel.bind(this)}
             wrapClassName={classNames}
             footer={[
               <Button key={'cancel'} onClick={this.handleCancel.bind(this)}>Cancel</Button>,
               <Button key={'submit'} className="positive" type="primary" loading={this.state.loading}
                       onClick={this.handleOk.bind(this)}>Mark as resolved</Button>,
             ]}>
        <div className="event-resolve-modal-header">
          <div className="event-resolve-modal-header-name">
            { this.props.event.get('name') }
          </div>
          <div className="event-resolve-modal-header-date">
            {time}
          </div>
        </div>
        <div className="event-resolve-modal-content">
          <Item label="Add comments(optional)">
            <Input type="textarea" rows="10"/>
          </Item>
        </div>
      </Modal>
    );
  }

}

export default MarkAsResolvedModal;
