import React from 'react';

class Name extends React.Component {
  static propTypes = {
    children: React.PropTypes.any
  };

  render() {

    return (
      <div className="product-metadata-item--preview--value">
        {
          typeof this.props.children === 'string' ? (
            this.props.children.split('\n').map((line, key) => (
              <span key={key}>{line}<br/></span>
            ))
          ) : (
            this.props.children
          )
        }
      </div>
    );
  }
}

export default Name;
