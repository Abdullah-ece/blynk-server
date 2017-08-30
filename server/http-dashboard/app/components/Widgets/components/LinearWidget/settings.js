import React from 'react';
import {
  Modal,
  SimpleContentEditable
} from 'components';
import {Row, Col, Button} from 'antd';

class LinearWidgetSettings extends React.Component {

  constructor(props) {
    super(props);

    this.onChange = this.onChange.bind(this);
    this.handleClick = this.handleClick.bind(this);
  }

  state = {
    value: 'Chart Title'
  };

  onChange(value) {
    this.setState({
      value: value
    });
  }

  handleClick() {
    this.setState({
      value: (Math.random()).toString()
    });
  }

  render() {
    return (
      <Modal width={'auto'}
             wrapClassName="modal-window-widget-settings"
             visible={true}
             closable={false}
             okText={'Save'}
             cancelText={'Close'}>
        <Row>
          <Col span={12} className="modal-window-widget-settings-config-column">
            <div className="modal-window-widget-settings-config-column-header">
              <SimpleContentEditable className="modal-window-widget-settings-config-widget-name"
                                     value={this.state.value}
                                     onChange={this.onChange}/>

              <div className="modal-window-widget-settings-config-add-source">
                <Button type="dashed" onClick={this.handleClick}>Add source</Button>
              </div>
            </div>
          </Col>
          <Col span={12} className="modal-window-widget-settings-preview-column">
            Awesome
          </Col>
        </Row>
      </Modal>
    );
  }

}

export default LinearWidgetSettings;
