import React from 'react';
import {Button} from 'antd';
export default class AddDataStreamsField extends React.Component {

  static propTypes = {
    onFieldAdd: React.PropTypes.func
  };

  render() {
    return (
      <div className="products-add-new-field">
        <div className="products-add-new-field-fields">
          <Button type="dashed" onClick={this.addField}>+ Add New Data Stream</Button>
        </div>
      </div>
    );
  }

}
