import React            from 'react';
import {Input, Item}    from 'components/UI';
import {Modal}          from 'components';
import {Button}         from 'antd';
import classnames       from 'classnames';
import moment           from 'moment';
import {EVENT_TYPES}    from 'services/Products';
import './styles.less';
import {reduxForm, reset, getFormValues} from 'redux-form';
import {bindActionCreators} from 'redux';
import {TimelineResolve} from 'data/Devices/api';
import {connect} from 'react-redux';

@connect((state) => ({
  account: state.Account,
  formValues: getFormValues('ResolveModal')(state)
}), (dispatch) => ({
  resetForm: bindActionCreators(reset, dispatch),
  resolve: bindActionCreators(TimelineResolve, dispatch)
}))
@reduxForm({
  form: 'ResolveModal'
})
class MarkAsResolvedModal extends React.Component {

  static propTypes = {
    onCancel: React.PropTypes.func,
    onMarkAsResolved: React.PropTypes.func,
    resetForm: React.PropTypes.func,
    resolve: React.PropTypes.func,
    onSuccess: React.PropTypes.func,
    event: React.PropTypes.object,
    formValues: React.PropTypes.object,
    account: React.PropTypes.object,
    deviceId: React.PropTypes.number,
    isModalVisible: React.PropTypes.bool,
  };

  state = {
    loading: false
  };

  componentWillUpdate(nextProps) {
    if (this.props.isModalVisible && !nextProps.isModalVisible) {
      this.props.resetForm('ResolveModal');
    }
  }

  handleOk() {

    this.setState({
      loading: true
    });
    this.props.resolve({
      orgId: this.props.account.orgId,
      deviceId: this.props.deviceId,
      eventId: this.props.event.id,
      comment: this.props.formValues && this.props.formValues.comment || ''
    }).then(() => {
      this.setState({
        loading: false
      });
      this.props.onSuccess();
    }).catch((err) => {
      Modal.error({
        title: 'Sorry, something went wrong',
        message: err && err.error && err.error.response.message
      });
    });
  }

  handleCancel() {
    this.props.onCancel();
  }

  render() {

    if (!this.props.isModalVisible)
      return null;

    const classNames = classnames({
      'event-resolve-modal': true,
      'event-resolve-modal--critical': this.props.event.eventType === EVENT_TYPES.CRITICAL,
      'event-resolve-modal--warning': this.props.event.eventType === EVENT_TYPES.WARNING,
    });

    const time = moment(this.props.event.ts).calendar(null, {
      sameDay: '[Today], hh:mm A',
      lastDay: '[Yesterday], hh:mm A',
      lastWeek: 'dddd, hh:mm A',
      sameElse: 'MMM D, YYYY hh:mm A'
    });

    return (
      <Modal visible={this.props.isModalVisible}
             confirmLoading={this.state.loading}
             onCancel={this.handleCancel.bind(this)}
             closable={false}
             wrapClassName={classNames}
             footer={[
               <Button key={'cancel'} onClick={this.handleCancel.bind(this)}>Cancel</Button>,
               <Button key={'submit'} className="positive" type="primary" loading={this.state.loading}
                       onClick={this.handleOk.bind(this)}>Mark as resolved</Button>,
             ]}>
        <div className="event-resolve-modal-header">
          <div className="event-resolve-modal-header-name">
            { this.props.event.name }
          </div>
          <div className="event-resolve-modal-header-date">
            {time}
          </div>
        </div>
        <div className="event-resolve-modal-content">
          <Item label="Add comments (optional)">
            <Input type="textarea" name="comment" rows="10"/>
          </Item>
        </div>
      </Modal>
    );
  }

}

export default MarkAsResolvedModal;
