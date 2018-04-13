import React from 'react';
import {Radio, Badge} from 'antd';
import './styles.less';
import {Field} from 'redux-form';
import {TIMELINE_TYPE_FILTERS} from 'services/Devices';

class TypeFiltering extends React.Component {

  static propTypes = {
    totalCritical: React.PropTypes.number,
    totalWarning: React.PropTypes.number,
    totalResolved: React.PropTypes.number,
  };

  component({input, totalCritical, totalWarning, totalResolved}) {

    return (
      <div className="devices--device-timeline--type-filtering">
        <Radio.Group {...input}>
          <Radio.Button value={TIMELINE_TYPE_FILTERS.ALL.key}>
            { TIMELINE_TYPE_FILTERS.ALL.value }
          </Radio.Button>
          <Radio.Button value={TIMELINE_TYPE_FILTERS.CRITICAL.key}>
            {TIMELINE_TYPE_FILTERS.CRITICAL.value} {totalCritical && (
            <Badge count={totalCritical} className="small critical"/>) || (null)}
          </Radio.Button>
          <Radio.Button value={TIMELINE_TYPE_FILTERS.WARNING.key}>
            {TIMELINE_TYPE_FILTERS.WARNING.value} {totalWarning && (
            <Badge count={totalWarning} className="small warning"/>) || (null)}
          </Radio.Button>
          <Radio.Button value={TIMELINE_TYPE_FILTERS.RESOLVED.key}>
            {TIMELINE_TYPE_FILTERS.RESOLVED.value} {totalResolved && (<Badge count={totalResolved} className="small positive"
                 overflowCount={999}/>) || (null)
            }
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
