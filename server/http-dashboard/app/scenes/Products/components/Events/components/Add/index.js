import React from 'react';
import {Button} from 'antd';
import './styles.less';

class Add extends React.Component {

  render() {
    return (
      <div className="products-add-new-field">
        <div className="products-add-new-field-title">+ Add new Event:</div>
        <div className="products-add-new-field-fields">
          <Button type="dashed" className="add-info-event-button">Info Event</Button>
          <Button type="dashed" className="add-warning-event-button">Warning Event</Button>
          <Button type="dashed" className="add-critical-event-button">Critical Event</Button>
        </div>
      </div>
    );
  }

}

export default Add;
