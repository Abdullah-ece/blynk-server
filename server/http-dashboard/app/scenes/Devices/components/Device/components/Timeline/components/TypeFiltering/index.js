import React from 'react';
import {Radio, Badge} from 'antd';
import './styles.less';
import {Field} from 'redux-form';
import {TIMELINE_TYPE_FILTERS} from 'services/Devices';

class TypeFiltering extends React.Component {

  component({input}) {

    return (
      <div className="devices--device-timeline--type-filtering">
        <Radio.Group {...input}>
          <Radio.Button value={TIMELINE_TYPE_FILTERS.ALL.key}>
            { TIMELINE_TYPE_FILTERS.ALL.value }
          </Radio.Button>
          <Radio.Button value={TIMELINE_TYPE_FILTERS.CRITICAL.value}>
            {TIMELINE_TYPE_FILTERS.CRITICAL.value} <Badge count={2} className="small critical"/>
          </Radio.Button>
          <Radio.Button value={TIMELINE_TYPE_FILTERS.WARNING.value}>
            {TIMELINE_TYPE_FILTERS.WARNING.value} <Badge count={25} className="small warning"/>
          </Radio.Button>
          <Radio.Button value={TIMELINE_TYPE_FILTERS.RESOLVED.value}>
            {TIMELINE_TYPE_FILTERS.RESOLVED.value} <Badge count={234} className="small positive" overflowCount={999}/>
          </Radio.Button>
        </Radio.Group>
      </div>
    );
  }

  render() {
    return (
      <Field {...this.props} component={this.component}/>
    );
  }

}

export default TypeFiltering;
