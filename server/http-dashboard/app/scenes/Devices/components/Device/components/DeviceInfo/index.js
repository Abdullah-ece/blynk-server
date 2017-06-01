import React from 'react';
import {Row, Col} from 'antd';
import {Fieldset, DeviceStatus, DeviceAuthToken} from 'components';
import './styles.less';

class DeviceInfo extends React.Component {

  render() {
    return (
      <div className="device--device-info">
        <Row>
          <Col span={8}>
            <Fieldset>
              <Fieldset.Legend>Status</Fieldset.Legend>
              <DeviceStatus status="online"/>
            </Fieldset>
            <Fieldset>
              <Fieldset.Legend>Auth Token</Fieldset.Legend>
              <DeviceAuthToken authToken={'ukngerbwm4792nwe3'}/>
            </Fieldset>
          </Col>
          <Col span={8}>
            <Fieldset>
              <Fieldset.Legend>Last Reported</Fieldset.Legend>
              Today, 12:35 AM
            </Fieldset>
            <Fieldset>
              <Fieldset.Legend>Organization</Fieldset.Legend>
              Blynk
            </Fieldset>
          </Col>
          <Col span={8}>
            <div className="device--device-info-logo">
              <img src="http://www.knightequip.com/images/product_warewash_nav/ump-hospitality.jpg"/>
            </div>
          </Col>
        </Row>
      </div>
    );
  }

}

export default DeviceInfo;
