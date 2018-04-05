import React from 'react';
import {ItemsGroup, Item} from 'components/UI';
import {MetadataSelect} from 'components/Form';
import PropTypes from 'prop-types';

class ListModal extends React.Component {

  static propTypes = {
    options: PropTypes.oneOfType([
      PropTypes.object,
      PropTypes.array,
    ])
  };

  render() {

    const notFoundContent = Object.keys(this.props.options).length > 0 ? 'No match' : 'No Options';

    return (
      <div>
        <ItemsGroup>
          <Item label="Value">
            <MetadataSelect notFoundContent={notFoundContent} allowZero={false} name="selectedOption" type="text" placeholder="Choose Option" values={this.props.options} style={{width: '100%'}}/>
          </Item>
        </ItemsGroup>
      </div>
    );
  }

}

export default ListModal;
