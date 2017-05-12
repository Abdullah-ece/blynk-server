import React from 'react';

class Preview extends React.Component {

  static propTypes = {
    valid: React.PropTypes.any,
    invalid: React.PropTypes.any,
    children: React.PropTypes.any,

    isEmpty: React.PropTypes.bool,
    isTouched: React.PropTypes.bool,
    isValid: React.PropTypes.bool
  };

  render() {
    return (
      <div className="product-events-event-preview">

        {
          (this.props.isValid && !this.props.isEmpty && (
            this.props.valid
          ))
        }

        {
          (!this.props.isValid && this.props.isTouched && (
            this.props.invalid
          ))
        }
      </div>
    );
  }

}

export default Preview;
