import React from 'react';
import {Radio, Badge} from 'antd';
import './styles.less';

class TypeFiltering extends React.Component {

  render() {
    return (
      <div className="devices--device-timeline--type-filtering">
        <Radio.Group>
          <Radio.Button value="all">
            All Events
          </Radio.Button>
          <Radio.Button value="CRITICAL">
            Critical <Badge count={2} className="small critical"/>
          </Radio.Button>
          <Radio.Button value="WARNING">
            Warning <Badge count={25} className="small warning"/>
          </Radio.Button>
          <Radio.Button value="RESOLVED">
            Resolved <Badge count={234} className="small positive" overflowCount={999}/>
          </Radio.Button>
        </Radio.Group>
      </div>
    );
  }

}

export default TypeFiltering;
