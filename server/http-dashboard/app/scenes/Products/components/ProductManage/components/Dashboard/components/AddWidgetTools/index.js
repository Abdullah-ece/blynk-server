import React      from 'react';
import {
  Button,
  Icon
}                 from 'antd';
import './styles.less';

class AddWidgetTools extends React.Component {

  render() {
    return (
      <div className="product-manage-dashboard--add-widget-tools">
        <div className="product-manage-dashboard--add-widget-tools-title">+ Add New Widget:</div>
        <div className="product-manage-dashboard--add-widget-tools-buttons">
          <Button.Group>
            <Button><Icon type="area-chart" /></Button>
            <Button disabled={true}><Icon type="pie-chart" /></Button>
            <Button disabled={true}><Icon type="dot-chart" /></Button>
            <Button disabled={true}><Icon type="close" /></Button>
            <Button disabled={true}><Icon type="close" /></Button>
            <Button disabled={true}><Icon type="close" /></Button>
            <Button disabled={true}><Icon type="close" /></Button>
          </Button.Group>
        </div>
      </div>
    );
  }

}

export default AddWidgetTools;
