import React from 'react';
import {
  Modal
} from 'components';
import {
  Button,
  Row,
  Col
} from 'antd';
import PropTypes from 'prop-types';

class WidgetSettings extends React.Component {

  static propTypes = {
    visible: PropTypes.bool,
    isSaveDisabled: PropTypes.bool,
    onCancel: PropTypes.func,
    onSave: PropTypes.func,
    config: PropTypes.element,
    preview: PropTypes.element,
  };

  render() {
    return (
      <Modal width={'auto'}
             wrapClassName="modal-window-widget-settings"
             visible={this.props.visible}
             onCancel={this.props.onCancel}
             closable={false}
             footer={[
               <Button key="back" onClick={this.props.onCancel}>
                 Close
               </Button>,
               <Button key="submit" type="primary" onClick={this.props.onSave} disabled={this.props.isSaveDisabled}>
                 Save
               </Button>,
             ]}>
        <Row type="flex">
          <Col span={12} className="modal-window-widget-settings-config-column">
            {this.props.config}
          </Col>
          <Col span={12} className="modal-window-widget-settings-preview-column">
            {this.props.preview}
          </Col>
        </Row>

      </Modal>
    );
  }

}

export default WidgetSettings;
