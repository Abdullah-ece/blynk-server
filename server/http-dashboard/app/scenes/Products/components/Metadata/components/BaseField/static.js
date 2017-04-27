import React from 'react';
import Metadata from 'scenes/Products/components/Metadata';


class BaseFieldStatic extends React.Component {

  constructor(props) {
    super(props);

    if (!this.component) {
      throw Error('BaseFieldStatic children should have component method');
    }
  }

  render() {

    const {Item} = Metadata;

    return (
      <Item.Static>
        { this.component() }
      </Item.Static>
    );
  }
}

export default BaseFieldStatic;
