import React from 'react';
import './styles.less';
import {Button} from 'antd';

class MetadataIntroductionMessage extends React.Component {

  static propTypes = {
    onGotItClick: React.PropTypes.func
  };

  render() {
    return (
      <div className="products-metadata-introduction-message">
        <div className="products-metadata-introduction-message-title">
          Add Metadata to your Product
        </div>
        <div className="products-metadata-introduction-message-content">
          Metadata is a set of characteristics (or configurations) applied to every Product and associated with every
          Device. This information will be used for the device provisioning and device management.
          <br/><br/>
          Metadata can be of different types. Choose the most relevant type for every characteristic.
          <br/><br/>
          For example, if you would need to associate Serial Number with every product, add the “Number” metadata field
          as shown below. Metadata is also used during device provisioning and configiration process. Staff, installing
          the equipment will be able to fill it in.
          <br/><br/>
          Metadata can be edited by users based on their access level.
          <br/><br/>
          <Button type="dashed" onClick={this.props.onGotItClick}>Got it</Button>
        </div>
      </div>
    );
  }
}

export default MetadataIntroductionMessage;
