import React from 'react';
import {Button} from 'antd';
import './styles.less';

class Dashboard extends React.Component {

  FILTERS = {
    HOUR: 'hour',
    DAY: 'day',
    WEEK: 'week',
    MONTH: 'month',
    CUSTOM: 'custom'
  };

  FILTER_BUTTONS = [{
    key: this.FILTERS.HOUR,
    value: '1 Hour'
  }, {
    key: this.FILTERS.DAY,
    value: '1 Day'
  }, {
    key: this.FILTERS.WEEK,
    value: '1 Week'
  }, {
    key: this.FILTERS.MONTH,
    value: 'Month'
  }, {
    key: this.FILTERS.CUSTOM,
    value: 'Custom Range'
  }];

  constructor(props) {
    super(props);

    this.state = {
      filter: this.FILTERS.HOUR
    };
  }

  filterBy(key) {
    this.setState({
      filter: key
    });
  }

  render() {
    return (
      <div>

        <Button.Group size="default" className="devices-device-dashboard-time-filter">
          { this.FILTER_BUTTONS.map((button, key) => (
            <Button key={key}
                    onClick={this.filterBy.bind(this, button.key)}
                    type={button.key === this.state.filter && 'primary' || 'default'}>
              {button.value}
            </Button>
          ))}
        </Button.Group>

      </div>
    );
  }

}

export default Dashboard;
