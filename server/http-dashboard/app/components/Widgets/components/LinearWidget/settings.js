import React from 'react';
import {
  Modal,
  SimpleContentEditable
} from 'components';
import {MetadataSelect as Select} from 'components/Form';
import {Item, ItemsGroup}      from "components/UI";
import {Row, Col, Button, Radio, Icon} from 'antd';
import {reduxForm} from 'redux-form';

@reduxForm({
  form: 'settings'
})
class LinearWidgetSettings extends React.Component {

  constructor(props) {
    super(props);

    this.onChange = this.onChange.bind(this);
    this.onChange2 = this.onChange2.bind(this);
    this.handleClick = this.handleClick.bind(this);
  }

  state = {
    value: 'Chart Title',
    value2: 'Temperature',
  };

  onChange(value) {
    this.setState({
      value: value
    });
  }

  onChange2(value) {
    this.setState({
      value2: value
    });
  }

  handleClick() {
    this.setState({
      value: (Math.random()).toString()
    });
  }

  render() {

    const types = [
      {
        key: 'Raw Data',
        value: 'Raw Data',
      },
      {
        key: 'AVG Value',
        value: 'AVG Value',
      }
    ];
    const sources = [
      {
        key: 'DataStream 1',
        value: 'DataStream 1',
      },
      {
        key: 'DataStream 2',
        value: 'DataStream 2',
      },
    ];

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
            <div className="modal-window-widget-settings-config-column-sources">
              <div className="modal-window-widget-settings-config-column-sources-source">
                <div className="modal-window-widget-settings-config-column-sources-source-header">
                  <SimpleContentEditable
                    className="modal-window-widget-settings-config-column-sources-source-header-name"
                    value={this.state.value2}
                    onChange={this.onChange2}/>
                  <div className="modal-window-widget-settings-config-column-sources-source-header-tools">
                    <Button size="small" icon="delete"/>
                    <Button size="small" icon="copy"/>
                    <Button size="small" icon="bars"/>
                  </div>
                </div>
                <div className="modal-window-widget-settings-config-column-sources-source-type-select">
                  <ItemsGroup>
                    <Item label="Source" offset="medium">
                      <Select name="type" displayError={false} values={types} placeholder="Source"
                              validate={[]}
                              style={{width: '100px'}}/>
                    </Item>
                    <Item label=" " offset="medium">
                      <Select name="source" displayError={false} values={sources} placeholder="Product"
                              validate={[]}
                              style={{width: '100%'}}/>
                    </Item>
                  </ItemsGroup>
                </div>
                <div className="modal-window-widget-settings-config-column-sources-source-chart-type">
                  <div className="modal-window-widget-settings-config-column-sources-source-chart-type-select">
                    <Item label="Chart Type: LINE" offset="medium">
                      <Radio.Group onChange={() => {}} defaultValue="a">
                        <Radio.Button value="a">
                          <Icon type="area-chart" />
                        </Radio.Button>
                        <Radio.Button value="c">
                          <Icon type="dot-chart" />
                        </Radio.Button>
                        <Radio.Button value="d">
                          <Icon type="bar-chart" />
                        </Radio.Button>
                      </Radio.Group>
                    </Item>
                  </div>
                </div>
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
